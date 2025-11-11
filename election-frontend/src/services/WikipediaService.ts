export async function getWikipediaSummary(query: string) {
  let response = await fetch(
    `https://nl.wikipedia.org/api/rest_v1/page/summary/${encodeURIComponent(query)}`
  )
  if (response.status === 404) {
    const searchUrl = `https://nl.wikipedia.org/w/api.php?action=query&list=search&srsearch=${encodeURIComponent(
      query,
    )}&format=json&origin=*`
    const searchRes = await fetch(searchUrl)
    const searchData = await searchRes.json()
    const firstTitle = searchData.query?.search?.[0]?.title

    if (firstTitle) {
      console.log(`Geen exacte match, probeer met titel: ${firstTitle}`)
      response = await fetch(
        `https://nl.wikipedia.org/api/rest_v1/page/summary/${encodeURIComponent(firstTitle)}`
      )
    }
  }
  const data = await response.json()
  console.log('Wikipedia data:', data)

  return {
    title: data.title,
    summary: data.extract,
    url: data.content_urls?.desktop?.page,
    image: data.thumbnail?.source,
  }
}

export async function getWikipediaPerson(name: string) {
  const url = `https://nl.wikipedia.org/api/rest_v1/page/summary/${encodeURIComponent(name)}`
  const res = await fetch(url)


  const data = await res.json()
  const firstTitle = data.query?.search?.[0]?.title

  if (firstTitle) {

  }
  return {
    title: data.title,
    description: data.description,
    image: data.thumbnail?.source || '',
  }
}

// Hulpfunctie: maak een directe Commons-bestands-URL met breedte
function commonsFileUrl(filename: string, width = 500) {
  // Wikidata geeft bij P154/P18 een bestandsnaam zoals "Logo VVD.svg"
  // Via Special:FilePath krijg je een directe, schaalbare URL
  return `https://commons.wikimedia.org/wiki/Special:FilePath/${encodeURIComponent(filename)}?width=${width}`;
}

export async function getWikipediaPartyData(query: string) {
  // 1) alias-map voor lastige afkortingen
  const aliasMap: Record<string, string> = {
    VVD: "Volkspartij voor Vrijheid en Democratie",
    CDA: "Christen-Democratisch Appèl",
    D66: "Democraten 66",
    PvdA: "Partij van de Arbeid",
    GL: "GroenLinks",
    PVV: "Partij voor de Vrijheid",
    BBB: "BoerBurgerBeweging",
    "LEF": "LEF – Voor de Nieuwe Generatie",
    "50PLUS": "50PLUS",
    "JA21": "JA21",
    "BIJ1": "BIJ1",
    "SP": "Socialistische Partij (Nederland)",
    "CU": "ChristenUnie",
    "PvdD": "Partij voor de Dieren",
    "Volt": "Volt Nederland",
    "SGP": "Staatkundig Gereformeerde Partij",
    "NSC": "Nieuw Sociaal Contract",
    "FvD": "Forum voor Democratie",
    "DENK": "DENK (politieke partij)",
    "Splinter": "Splinter (politieke partij)",
  };

  // API endpoints
  const WIKI_REST = "https://nl.wikipedia.org/api/rest_v1";
  const WIKI_ACTION = "https://nl.wikipedia.org/w/api.php";
  const WIKIDATA = "https://www.wikidata.org/w/api.php";

  // ---- helpers
  async function restSummary(title: string) {
    const res = await fetch(`${WIKI_REST}/page/summary/${encodeURIComponent(title)}`);
    if (!res.ok) return null;
    const d = await res.json();
    return {
      title: d.title as string,
      url: d?.content_urls?.desktop?.page as string | undefined,
      summary: (d.extract as string) ?? "",
      img: d?.thumbnail?.source as string | undefined,
      wikibase: d?.wikibase_item as string | undefined, // Q-id als die mee komt
    };
  }

  async function searchBestTitle(raw: string) {
    const searchTerm = aliasMap[raw] || `${raw} politieke partij`;
    const params = new URLSearchParams({
      action: "query",
      list: "search",
      srsearch: searchTerm,
      srnamespace: "0",
      srlimit: "10",
      format: "json",
      origin: "*",
    });
    const r = await fetch(`${WIKI_ACTION}?${params}`);
    const j = await r.json();
    const results: Array<{ title: string }> = j?.query?.search ?? [];
    if (!results.length) return null;

    // eerst proberen exacte/sterke matches
    const qLower = raw.toLowerCase();
    const partyHit =
      results.find(x => x.title.toLowerCase().includes("(politieke partij)")) ||
      results.find(x => x.title.toLowerCase() === aliasMap[raw]?.toLowerCase()) ||
      results.find(x => x.title.toLowerCase() === qLower) ||
      results.find(x => x.title.toLowerCase().includes(qLower));

    return (partyHit ?? results[0]).title;
  }

  async function getWikidataIdForTitle(title: string) {
    const params = new URLSearchParams({
      action: "query",
      prop: "pageprops",
      titles: title,
      format: "json",
      origin: "*",
    });
    const r = await fetch(`${WIKI_ACTION}?${params}`);
    const j = await r.json();
    const pages = j?.query?.pages;
    if (!pages) return null;
    const first = Object.values(pages)[0] as any;
    return first?.pageprops?.wikibase_item || null; // Q-id
  }

  async function getImageFromWikidata(qid: string) {
    // Haal claims (P154 logo, P18 image)
    const params = new URLSearchParams({
      action: "wbgetentities",
      ids: qid,
      props: "claims",
      format: "json",
      origin: "*",
    });
    const r = await fetch(`${WIKIDATA}?${params}`);
    const j = await r.json();
    const entity = j?.entities?.[qid];
    const claims = entity?.claims ?? {};

    // P154 (logo) eerst proberen
    const p154 = claims?.P154?.[0]?.mainsnak?.datavalue?.value;
    if (typeof p154 === "string") return commonsFileUrl(p154);

    // anders P18 (normale afbeelding)
    const p18 = claims?.P18?.[0]?.mainsnak?.datavalue?.value;
    if (typeof p18 === "string") return commonsFileUrl(p18);

    return null;
  }

  // ---- hoofdlogica

  // 1) bepaal best passende titel
  const bestTitle = await searchBestTitle(query);
  if (!bestTitle) return null;

  // 2) probeer REST summary (heeft vaak meteen thumbnail + summary)
  let data = await restSummary(bestTitle);

  // 3) Controle: lijkt het op een partij? en hebben we een img?
  const looksParty =
    !!data &&
    (data.title.toLowerCase().includes("partij") ||
      (data.summary ?? "").toLowerCase().includes("partij"));

  let img = data?.img;
  let summary = data?.summary ?? "";
  let title = data?.title ?? bestTitle;
  let url = data?.url;

  // 4) Als geen img → probeer via Wikidata (logo P154 of image P18)
  if (!img) {
    const qid = data?.wikibase || (await getWikidataIdForTitle(bestTitle));
    if (qid) {
      const wdImg = await getImageFromWikidata(qid);
      if (wdImg) img = wdImg;
    }
  }

  // 5) Als nog steeds geen goede "partij" match of geen img, doe een tweede poging met expliciete titel
  if ((!looksParty || !img) && !bestTitle.toLowerCase().includes("(politieke partij)")) {
    const forcedTitle = `${bestTitle} (politieke partij)`;
    const forced = await restSummary(forcedTitle);
    if (forced?.summary) {
      title = forced.title ?? forcedTitle;
      summary = forced.summary ?? summary;
      url = forced.url ?? url;
      if (!img) img = forced.img; // alleen overschrijven als we nog geen img hadden
      if (!img) {
        const qid = forced?.wikibase || (await getWikidataIdForTitle(forcedTitle));
        if (qid) {
          const wdImg = await getImageFromWikidata(qid);
          if (wdImg) img = wdImg;
        }
      }
    }
  }

  return {
    title,
    summary: summary || "Geen samenvatting gevonden.",
    img: img || "",
    url: url || `https://nl.wikipedia.org/wiki/${encodeURIComponent(title)}`,
  };
}



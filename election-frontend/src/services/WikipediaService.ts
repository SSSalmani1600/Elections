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

export async function getWikipediaPartyData(query: string) {
  const endpoint = "https://nl.wikipedia.org/w/api.php";

  const searchParams = new URLSearchParams({
    action: "query",
    list: "search",
    srsearch: query,
    format: "json",
    origin: "*",
  });

  const searchResponse = await fetch(`${endpoint}?${searchParams}`);
  const searchData = await searchResponse.json();

  if (!searchData.query?.search?.length) {
    console.warn("Geen resultaten gevonden");
    return null;
  }

  const pageTitle = searchData.query.search[0].title;

  const infoParams = new URLSearchParams({
    action: "query",
    titles: pageTitle,
    prop: "pageimages|extracts",
    piprop: "thumbnail",
    pithumbsize: "500",
    exintro: "true",
    explaintext: "true",
    format: "json",
    origin: "*",
  });

  const infoResponse = await fetch(`${endpoint}?${infoParams}`);
  const infoData = await infoResponse.json();

  const pages = infoData.query?.pages;
  const firstPage = pages ? Object.values(pages)[0] as any : null;

  console.log(firstPage)
  return {
    title: pageTitle,
    summary: firstPage?.extract || "Geen samenvatting gevonden.",
    img: firstPage?.thumbnail?.source || "",
  };
}


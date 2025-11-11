export async function getWikipediaSummary(query: string) {
  const restUrl = `https://nl.wikipedia.org/api/rest_v1/page/summary/${encodeURIComponent(query)}`
  let restRes = await fetch(restUrl)
  let restData = await restRes.json()

  if (
    restRes.status === 404 ||
    restData.type === 'https://mediawiki.org/wiki/HyperSwitch/errors/not_found'
  ) {
    const searchUrl = `https://nl.wikipedia.org/w/api.php?action=query&list=search&srsearch=${encodeURIComponent(query)}&format=json&origin=*`
    const searchRes = await fetch(searchUrl)
    const searchData = await searchRes.json()
    const firstTitle = searchData.query?.search?.[0]?.title

    if (firstTitle) {
      console.log(`Geen exacte match, probeer met titel: ${firstTitle}`)
      restRes = await fetch(
        `https://nl.wikipedia.org/api/rest_v1/page/summary/${encodeURIComponent(firstTitle)}`,
      )
      restData = await restRes.json()
    }
  }

  const fullUrl = `https://nl.wikipedia.org/w/api.php?action=query&prop=extracts&explaintext=true&exintro=false&format=json&origin=*&titles=${encodeURIComponent(
    restData.title || query,
  )}`
  const fullRes = await fetch(fullUrl)
  const fullData = await fullRes.json()

  type WikiPage = {
    extract?: string
    title?: string
  }

  const pages = (fullData.query?.pages ?? {}) as Record<string, WikiPage>
  const firstPage = Object.values(pages)[0]
  const longExtract = firstPage?.extract || restData.extract

  return {
    title: restData.title,
    summary: longExtract,
    url: restData.content_urls?.desktop?.page,
    image: restData.thumbnail?.source,
    description: restData.description,
  }
}

export async function getWikipediaPerson(name: string) {
  const url = `https://nl.wikipedia.org/api/rest_v1/page/summary/${encodeURIComponent(name)}`
  const res = await fetch(url)
  const data = await res.json()
  return {
    title: data.title,
    description: data.description,
    image: data.thumbnail?.source || '',
  }
}

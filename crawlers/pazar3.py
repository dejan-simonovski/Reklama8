import json

import requests
from bs4 import BeautifulSoup

BASE_URL = "https://www.pazar3.mk"
SEARCH_URL = f"{BASE_URL}/oglasi/?Page="

def scrape_page(page_number):
    url = SEARCH_URL + str(page_number)
    response = requests.get(url)
    soup = BeautifulSoup(response.text, "html.parser")
    listings = []

    for ad in soup.find_all("div", class_="new row row-listing"):
        title_elem = ad.find("a", class_="Link_vis")
        price_elem = ad.find("p", class_="list-price")
        location_elem = ad.find_all("a", class_="link-html nobold")[-1]
        img_elem = ad.find("img", class_="ProductionImg")
        category_elem = ad.find("a", class_="link-html5 nobold")

        if title_elem and price_elem and location_elem and img_elem:
            title = title_elem.text.strip()
            link = BASE_URL + title_elem["href"]
            price = price_elem.text.strip().replace("\n", " ")
            location = location_elem.text.strip()

            img_url = img_elem.get("data-src", img_elem.get("src", ""))

            category = category_elem.text.strip() if category_elem else "Unknown"

            listings.append({
                "title": title,
                "price": price,
                "location": location,
                "category": category,
                "image": img_url,
                "link": link
            })

    return listings, soup

def scrape_multiple_pages(limit=None):
    page = 1
    all_listings = []

    while True:
        print(f"Scraping page {page}...")
        listings, soup = scrape_page(page)
        all_listings.extend(listings)

        if limit and page >= limit:
            break

        next_page_elem = soup.find("a", class_="page-link", string="Следна")
        if not next_page_elem:
            break

        page += 1

    return all_listings


# Example usage
if __name__ == "__main__":
    SCRAPE_LIMIT = 5  # Set to desired number
    results = scrape_multiple_pages(SCRAPE_LIMIT)

    print(f"Total listings scraped: {len(results)}")
    for listing in results[:5]:  # Print first 5 for visibility
        print(listing)

    SCRAPE_LIMIT = 5  # Set to desired number
    results = scrape_multiple_pages(SCRAPE_LIMIT)

    print(f"Total listings scraped: {len(results)}")

    # Save to file
    with open("pazar3_listings.json", "w", encoding="utf-8") as file:
        json.dump(results, file, ensure_ascii=False, indent=4)

    print(f"Results have been saved to 'pazar3_listings.json'")

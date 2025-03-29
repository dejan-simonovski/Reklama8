import json

import requests
from bs4 import BeautifulSoup

BASE_URL = "https://reklama5.mk"
SEARCH_URL = f"{BASE_URL}/Search?page="

def scrape_page(page_number):
    url = SEARCH_URL + str(page_number)
    response = requests.get(url)
    soup = BeautifulSoup(response.text, "html.parser")
    listings = []

    for ad in soup.find_all("div", class_="ad-desc-div"):
        title_elem = ad.find("a", class_="SearchAdTitle")
        price_elem = ad.find("span", class_="search-ad-price")
        location_elem = ad.find("span", class_="city-span")
        img_elem = ad.find_previous("div", class_="ad-image")
        category_elem = ad.find("a", class_="text-secondary")

        if title_elem and price_elem and location_elem and img_elem:
            title = title_elem.text.strip()
            link = BASE_URL + title_elem["href"]
            price = price_elem.text.strip()
            location = location_elem.text.strip()
            category = category_elem.text.strip()

            img_url = img_elem["style"].split("url(")[1].split(")")[0]
            if img_url.startswith("//"):
                img_url = "https:" + img_url

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

        next_page_elem = soup.find("a", title="Следна")
        if not next_page_elem:
            break

        page += 1

    return all_listings


if __name__ == "__main__":
    SCRAPE_LIMIT = 5  # Set to desired number of pages
    results = scrape_multiple_pages(SCRAPE_LIMIT)

    print(f"Total listings scraped: {len(results)}")
    for listing in results[:5]:  # Print first 5 for visibility
        print(listing)

    # Save to file
    with open("reklama5_listings.json", "w", encoding="utf-8") as file:
        json.dump(results, file, ensure_ascii=False, indent=4)

    print(f"Results have been saved to 'reklama5_listings.json'")

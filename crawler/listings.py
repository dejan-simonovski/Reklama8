import json
import random
import requests
from bs4 import BeautifulSoup

# Constants
SOURCES = [
    {
        "name": "pazar3",
        "base_url": "https://www.pazar3.mk",
        "search_url": "https://www.pazar3.mk/oglasi/?Page=",
    },
    {
        "name": "reklama5",
        "base_url": "https://reklama5.mk",
        "search_url": "https://reklama5.mk/Search?page=",
    },
]


def scrape_pazar3(page_number):
    print(f"Scraping page {page_number} on pazar3...")
    url = SOURCES[0]["search_url"] + str(page_number)
    response = requests.get(url)
    if response.status_code == 404:
        print(f"Page {page_number} on pazar3 does not exist. Stopping.")
        return [], False
    soup = BeautifulSoup(response.text, "html.parser")
    listings = []

    for ad in soup.find_all("div", class_="new row row-listing"):
        title_elem = ad.find("a", class_="Link_vis")
        price_elem = ad.find("p", class_="list-price")
        location_elem = ad.find_all("a", class_="link-html nobold")[-1]
        img_elem = ad.find("img", class_="ProductionImg")
        category_elem = ad.find("a", class_="link-html5 nobold")
        time_elem = ad.find("span", class_="pull-right ci-text-right")

        if title_elem and price_elem and location_elem and img_elem:
            listings.append({
                "title": title_elem.text.strip(),
                "price": price_elem.text.strip().replace("\n", " "),
                "location": location_elem.text.strip(),
                "category": category_elem.text.strip() if category_elem else "Unknown",
                "image": img_elem.get("data-src", img_elem.get("src", "")),
                "link": SOURCES[0]["base_url"] + title_elem["href"],
                "time": time_elem.text.strip() if time_elem else "Unknown",
                "source": "Pazar3",
            })
    return listings, bool(listings)


def scrape_reklama5(page_number):
    print(f"Scraping page {page_number} on reklama5...")
    url = SOURCES[1]["search_url"] + str(page_number)
    response = requests.get(url)
    if response.status_code == 404:
        print(f"Page {page_number} on reklama5 does not exist. Stopping.")
        return [], False
    soup = BeautifulSoup(response.text, "html.parser")
    listings = []

    for ad in soup.find_all("div", class_="ad-desc-div"):
        title_elem = ad.find("a", class_="SearchAdTitle")
        price_elem = ad.find("span", class_="search-ad-price")
        location_elem = ad.find("span", class_="city-span")
        img_elem = ad.find_previous("div", class_="ad-image")
        category_elem = ad.find("a", class_="text-secondary")
        time_elem = ad.find_next("div", class_="ad-date-div-1")
        if time_elem:
            time_text = " ".join(time_elem.stripped_strings)
        else:
            time_text = "Unknown"
        if price_elem:
            price_text = " ".join(price_elem.stripped_strings)
        else:
            price_text = "Unknown"

        if title_elem and price_elem and location_elem and img_elem:
            img_url = img_elem["style"].split("url(")[1].split(")")[0]
            if img_url.startswith("//"):
                img_url = "https:" + img_url

            listings.append({
                "title": title_elem.text.strip(),
                "price": " ".join(price_text.split()),
                "location": location_elem.text.strip(),
                "category": category_elem.text.strip() if category_elem else "Unknown",
                "image": img_url,
                "link": SOURCES[1]["base_url"] + title_elem["href"],
                "time": " ".join(time_text.split()),
                "source": "Reklama5"
            })
    return listings, bool(soup.find("a", title="Следна"))


def scrape_multiple_pages(scrape_func, limit=5):
    page = 1
    all_listings = []
    while page <= limit:
        listings, has_more = scrape_func(page)
        if not listings and not has_more:
            break
        all_listings.extend(listings)
        page += 1
    return all_listings


if __name__ == "__main__":
    SCRAPE_LIMIT = 5
    results = scrape_multiple_pages(scrape_pazar3, SCRAPE_LIMIT) + scrape_multiple_pages(scrape_reklama5, SCRAPE_LIMIT)

    print(f"Total listings scraped: {len(results)}")

    random.shuffle(results) # Shuffle

    with open("listings.json", "w", encoding="utf-8", errors="replace") as file:
        json.dump(results, file, ensure_ascii=False, indent=4)

    print("Results saved to 'listings.json'")
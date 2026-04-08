import json
from pathlib import Path


FOOD_ITEMS = [
    {
        "name": "Cheese Burger",
        "description": "Beef burger with cheddar cheese and house sauce",
        "price": 59000,
        "category": "Burger",
        "imageUrl": "https://loremflickr.com/640/480/burger?lock=201",
        "available": True,
    },
    {
        "name": "Double Beef Burger",
        "description": "Double beef patty burger for hungry customers",
        "price": 79000,
        "category": "Burger",
        "imageUrl": "https://loremflickr.com/640/480/burger?lock=202",
        "available": True,
    },
    {
        "name": "Pepperoni Pizza",
        "description": "Classic pizza with pepperoni topping",
        "price": 129000,
        "category": "Pizza",
        "imageUrl": "https://loremflickr.com/640/480/pizza?lock=203",
        "available": True,
    },
    {
        "name": "Seafood Pizza",
        "description": "Pizza topped with shrimp and squid",
        "price": 149000,
        "category": "Pizza",
        "imageUrl": "https://loremflickr.com/640/480/pizza?lock=204",
        "available": True,
    },
    {
        "name": "Fried Chicken",
        "description": "Crispy fried chicken combo",
        "price": 89000,
        "category": "Chicken",
        "imageUrl": "https://loremflickr.com/640/480/fried-chicken?lock=205",
        "available": True,
    },
    {
        "name": "Spaghetti Carbonara",
        "description": "Creamy spaghetti with bacon and parmesan",
        "price": 99000,
        "category": "Pasta",
        "imageUrl": "https://loremflickr.com/640/480/pasta?lock=206",
        "available": True,
    },
    {
        "name": "Caesar Salad",
        "description": "Fresh salad with lettuce, cheese and dressing",
        "price": 65000,
        "category": "Salad",
        "imageUrl": "https://loremflickr.com/640/480/salad?lock=207",
        "available": True,
    },
    {
        "name": "Matcha Latte",
        "description": "Iced matcha latte for demo menu",
        "price": 45000,
        "category": "Drink",
        "imageUrl": "https://loremflickr.com/640/480/drink?lock=208",
        "available": True,
    },
]


def build_sql(items):
    statements = []
    for item in items:
        name = item["name"].replace("'", "''")
        description = item["description"].replace("'", "''")
        category = item["category"].replace("'", "''")
        image_url = item["imageUrl"].replace("'", "''")
        available = "true" if item["available"] else "false"
        statements.append(
            "INSERT INTO food_items (name, description, price, category, image_url, available, created_at, updated_at) "
            f"VALUES ('{name}', '{description}', {item['price']:.2f}, '{category}', '{image_url}', {available}, NOW(), NOW());"
        )
    return "\n".join(statements)


def main():
    output_dir = Path(__file__).resolve().parent
    json_path = output_dir / "food-items.generated.json"
    sql_path = output_dir / "food-items.generated.sql"

    json_path.write_text(json.dumps(FOOD_ITEMS, indent=2), encoding="utf-8")
    sql_path.write_text(build_sql(FOOD_ITEMS), encoding="utf-8")

    print(f"Generated: {json_path}")
    print(f"Generated: {sql_path}")


if __name__ == "__main__":
    main()

INSERT INTO food_items (name, description, price, category, image_url, available, created_at, updated_at)
SELECT 'Cheese Burger', 'Beef burger with cheese and sauce', 59000.00, 'Burger',
       'https://loremflickr.com/640/480/burger?lock=101', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM food_items WHERE name = 'Cheese Burger');

INSERT INTO food_items (name, description, price, category, image_url, available, created_at, updated_at)
SELECT 'Pepperoni Pizza', 'Classic pizza with pepperoni and mozzarella', 129000.00, 'Pizza',
       'https://loremflickr.com/640/480/pizza?lock=102', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM food_items WHERE name = 'Pepperoni Pizza');

INSERT INTO food_items (name, description, price, category, image_url, available, created_at, updated_at)
SELECT 'Fried Chicken', 'Crispy fried chicken combo', 89000.00, 'Chicken',
       'https://loremflickr.com/640/480/fried-chicken?lock=103', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM food_items WHERE name = 'Fried Chicken');

INSERT INTO food_items (name, description, price, category, image_url, available, created_at, updated_at)
SELECT 'Spaghetti Carbonara', 'Creamy spaghetti with bacon and parmesan', 99000.00, 'Pasta',
       'https://loremflickr.com/640/480/pasta?lock=104', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM food_items WHERE name = 'Spaghetti Carbonara');

INSERT INTO food_items (name, description, price, category, image_url, available, created_at, updated_at)
SELECT 'Matcha Latte', 'Iced matcha latte for demo menu', 45000.00, 'Drink',
       'https://loremflickr.com/640/480/drink?lock=105', true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM food_items WHERE name = 'Matcha Latte');

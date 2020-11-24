# GimmeFood

Mobile Computing class at UT final project

## Authors

- Brandon Autrey
- Mai Ristoija
- Miras Kenzhegaliyev

## Idea for Restaurant Menu App

### Customer Use Case

Go in to a restaurant, sit down at a table, and scan the QR code on the table. The restaurant’s menu pops up and lets you add dishes to your order and pay for them. The dish is brought to your table when ready.

### Restaurant Use Case

Lettings customers find their own seat and order their own food without anything delivered to them, reduces my staff costs. When a dish is no longer available, I can notify the backend and the customers will no longer be able to order that dish.

### UI Screens

- QR code scanner
- Display menu
- Add dishes to order
- (extra) Pay for meal

### Features

- Camera API to read QR code to know where to fetch menu data
- Persistent Storage using Firebase to store menu data of the dishes
- Web REST API to retrieve menu data from firebase to display in app
- (extra) Use Language Localization to retrieve menu in app settings language
- (extra) Use Stripe API / Google Pay to do test payments

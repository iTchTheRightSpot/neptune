# API documentation

# Inventory module

### POST INVENTORY

Creates a new Inventory.

**Endpoint:** /api/v1/auth/register

**Request Body:**

```json
{
  "name": "T-shirt",
  "qty": 4
}
```

Response:

- 201 Created.
- 409 Conflict: Error creating inventory due to duplicate name, etc.

### GET INVENTORIES

Retrieves all the inventory

**Endpoint:** /api/v1/inventory/all

Response:

- 200 OK.

```json
[
  {
    "product_id": "6f9f4360-bf97-4c69-947b-2a62a03a700d",
    "name": "product-1",
    "qty": 2
  },
  {
    "product_id": "6f934360-bf97-4c69-947b-2a62a03a700f",
    "name": "product-3",
    "qty": 4
  }
]
```

### GET INVENTORY

Retrieves an inventory by its id

**Endpoint:** /api/v1/inventory/6f934360-bf97-4c69-947b-2a62a03a700f

Response:

- 200 OK.
- 404 Bad Request: If id isn't in `UUID` format.
- 404 Not Found: If no inventory associated to id.

```json
{
  "product_id": "6f934360-bf97-4c69-947b-2a62a03a700f",
  "name": "product-3",
  "qty": 4
}
```

## Order module

### POST ORDER

Creates a new order.

**Endpoint:** /api/v1/order

**Request Body:**
***status can be either PENDING or CONFIRMED***


```json
{
  "product_id": "6f9f4360-bf97-4c69-947b-2a62a03a700d",
  "qty": 4,
  "status": "PENDING"
}
```

Response:

- 201 Created.
- 404 Not Found: Invalid product id.
- 400 Bad Request: Inventory is out of stock.
- 409 Conflict: Inventory stock/qty is less than order qty or issue saving to order table.

### GET ORDERS

Retrieves all the orders

**Endpoint:** /api/v1/order

Response:

- 200 OK.

```json
[
  {
    "order_id": "6f9f4360-bf97-4c69-947b-2a62a03a700d",
    "product_id": "6f9f4321-bf97-4c69-947b-2a62a03a700d",
    "qty": 2,
    "status": "CONFIRMED"
  },
  {
    "order_id": "6f2f4360-bf97-4c69-947b-2a62a03a700d",
    "product_id": "7g2f4360-bf97-4c69-947b-2a62a03a700d",
    "qty": 2,
    "status": "PENDING"
  }
]
```
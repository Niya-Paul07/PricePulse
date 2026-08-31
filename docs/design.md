# System Design — Price Pulse

## Class Diagram

```mermaid
classDiagram
    class User {
        -int id
        -String name
        -String email
        +submitPriceEntry()
    }

    class Shop {
        -int id
        -String name
        -String location
        +getPriceEntries()
    }

    class Item {
        -int id
        -String name
        -String category
        -String unit
        +getPriceHistory()
    }

    class PriceEntry {
        -int id
        -Item item
        -Shop shop
        -User user
        -double price
        -Date date
        +isValid()
    }

    class PriceAnalyzer {
        <<abstract>>
        #List~PriceEntry~ data
        +analyze()*
    }

    class TrendAnalyzer {
        +getTrend(Item)
    }

    class SpikeDetector {
        +detectSpike(Item, threshold)
    }

    class CategorySummarizer {
        +summarizeByCategory(Category)
    }

    class PriceTrackerService {
        -List~Item~ items
        -List~Shop~ shops
        -List~PriceEntry~ entries
        +getCheapestShop(Item)
        +loadData()
        +saveData()
    }

    PriceAnalyzer <|-- TrendAnalyzer
    PriceAnalyzer <|-- SpikeDetector
    PriceAnalyzer <|-- CategorySummarizer

    Shop "1" --> "many" PriceEntry
    Item "1" --> "many" PriceEntry
    User "1" --> "many" PriceEntry
    PriceTrackerService --> PriceAnalyzer : uses
    PriceTrackerService --> PriceEntry : holds
```

## Schema

| Table/File | Fields |
|---|---|
| `items` | id (PK), name, category, unit |
| `shops` | id (PK), name, location |
| `users` | id (PK), name, email |
| `price_entries` | id (PK), item_id (FK→items), shop_id (FK→shops), user_id (FK→users), price, date |

## Rules
- A `PriceEntry` always requires `item_id`, `shop_id`, and `user_id` — no nulls.
- Storage model: in-memory during runtime, loaded once at startup (`loadData()`) and saved on exit (`saveData()`). CRUD operations act on in-memory objects, not directly on storage.
- `PriceTrackerService` composes `PriceAnalyzer` subclasses — it does not extend them.
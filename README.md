# PricePulse

A group OOP project that tracks item prices across shops — CRUD, cheapest-shop
lookup, trend analysis, and price-change detection, behind a Login/Sign Up.

## Team & Roles
| Name | Role |
|---|---|
| Niya | Coordination, integration, final packaging + Login/Sign Up |
| Anupa | System Design: class diagrams, DB schema |
| Devananda | UI/UX Design: wireframes, screen flow |
| Devaprabha | Product & Shop CRUD |
| Vaishnavi | Price Entry CRUD |
| Amith | QA / Tester |
| Aswin | Trend Analysis (Part A) |
| Shreya | Cheapest Shop Lookup |
| Devipriya | Documentation + Price-Change Detection (Part B) |

See [`docs/design.md`](docs/design.md) for the full class diagram and schema.

## Project Structure
UI is **Java Swing**.
```
src/main/java/com/pricepulse/
  model/       -> User, Shop, Item, PriceEntry            (done)
  auth/        -> AuthService (Login/Sign Up logic)        (done)
  ui/          -> AuthWindow (login/signup screen),
                  MainMenuFrame (tabbed home screen)        (done)
  crud/        -> Product & Shop CRUD, Price Entry CRUD     (TODO: Devaprabha, Vaishnavi)
  analysis/    -> TrendAnalyzer, SpikeDetector,
                  CategorySummarizer                        (TODO: Aswin, Devipriya)
  Main.java    -> entry point, wires everything together    (integration, Niya)
data/          -> CSV files used for persistence at runtime (git-ignored)
docs/          -> design.md, and any docs from Devipriya
```

### Swing convention for teammates
Build your feature as a class that **extends `JPanel`** (e.g.
`public class ProductShopPanel extends JPanel`), do your layout/logic inside
it, and hand it to Niya. It gets added to `MainMenuFrame` as one new tab —
see the TODO comments in `MainMenuFrame.java`.

## How to build & run
Requires JDK 17+.

```bash
# from the project root
find src -name "*.java" > sources.txt
javac -d out @sources.txt
java -cp out com.pricepulse.Main
```

## For teammates adding a module
1. Create your class(es) in the matching package under `src/main/java/com/pricepulse/...`
   (e.g. `com.pricepulse.crud.ProductShopCRUD` for Product & Shop CRUD).
2. Build on the shared model classes already in `com.pricepulse.model`
   (`User`, `Shop`, `Item`, `PriceEntry`) — don't create your own versions of these.
3. Work on your own branch, then open a PR into `main`.
4. Ping Niya once it's ready to wire into `Main.java`.

## Data storage
Following the design doc: everything lives in memory during runtime,
loaded once at startup and saved on exit. `AuthService` already does this
for users (`data/users.csv`) — CRUD modules should follow the same pattern
for items/shops/price entries (e.g. `data/items.csv`, `data/shops.csv`,
`data/price_entries.csv`).

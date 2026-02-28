Created three scripts in backend/scripts/:

test-unit.sh — runs only unit tests (service tests + context load)
test-api.sh — runs only Karate API component tests
test-all.sh — runs the full suite (unit + API)
Usage from anywhere:


Usage from anywhere:

```bash
./backend/scripts/test-unit.sh
./backend/scripts/test-api.sh
./backend/scripts/test-all.sh
```
#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

echo "=== Running Full Test Suite (Unit + API) ==="
cd "$PROJECT_DIR"
./mvnw test -q

echo ""
echo "=== Full Test Suite Complete ==="

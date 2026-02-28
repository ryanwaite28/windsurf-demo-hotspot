#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

echo "=== Running Karate API Tests ==="
cd "$PROJECT_DIR"
./mvnw test -Dtest="com.hotspot.karate.ApiTest" -q

echo ""
echo "=== API Tests Complete ==="

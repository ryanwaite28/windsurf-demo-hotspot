#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

echo "=== Running Unit Tests ==="
cd "$PROJECT_DIR"
./mvnw test -Dtest="com.hotspot.core.**,com.hotspot.HotSpotApplicationTests" -q

echo ""
echo "=== Unit Tests Complete ==="

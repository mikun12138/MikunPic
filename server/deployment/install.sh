#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
JAR_URL="${JAR_URL:-https://github.com/mikun12138/MikunPic/releases/download/v0.2.0/mikunpic-0.2.0.jar}"
JAR_NAME="${JAR_NAME:-mikunpic.jar}"
SERVICE_NAME="${SERVICE_NAME:-mikunpic}"
WORKING_DIR="${MIKUNPIC_HOME:-/opt/mikunpic}"
RUN_USER="${MIKUNPIC_RUN_USER:-${SUDO_USER:-root}}"
RUN_GROUP="${MIKUNPIC_RUN_GROUP:-$(id -gn "$RUN_USER")}"
SERVICE_FILE="/etc/systemd/system/$SERVICE_NAME.service"
SERVICE_TEMPLATE="$SCRIPT_DIR/mikunpic.service"
SOURCE_JAR="${SOURCE_JAR:-}"

if [[ "$(id -u)" -ne 0 ]]; then
    echo "System install requires root. Run: sudo $0" >&2
    exit 1
fi

mkdir -p "$WORKING_DIR"

if [[ -n "$SOURCE_JAR" ]]; then
    if [[ -f "$SOURCE_JAR" ]]; then
        cp "$SOURCE_JAR" "$WORKING_DIR/$JAR_NAME"
    else
        curl -fL "$SOURCE_JAR" -o "$WORKING_DIR/$JAR_NAME"
    fi
else
    curl -fL "$JAR_URL" -o "$WORKING_DIR/$JAR_NAME"
fi
chown -R "$RUN_USER:$RUN_GROUP" "$WORKING_DIR"

sed -e "s|{{WORKING_DIR}}|$WORKING_DIR|g" \
    -e "s|{{JAR_NAME}}|$JAR_NAME|g" \
    -e "s|{{SERVICE_NAME}}|$SERVICE_NAME|g" \
    -e "s|{{USER}}|$RUN_USER|g" \
    -e "s|{{GROUP}}|$RUN_GROUP|g" \
    "$SERVICE_TEMPLATE" > "$SERVICE_FILE"

systemctl daemon-reload
systemctl enable --now "$SERVICE_NAME.service"

echo "Installed $SERVICE_NAME system service"
echo "Working directory: $WORKING_DIR"
echo "Service file: $SERVICE_FILE"
echo "Run as: $RUN_USER:$RUN_GROUP"

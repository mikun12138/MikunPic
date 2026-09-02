#!/usr/bin/env bash
set -euo pipefail

APP_NAME="mikunpic"
JAR_NAME="${JAR_NAME:-mikunpic.jar}"
SERVICE_NAME="${SERVICE_NAME:-mikunpic}"
WORKING_DIR="${MIKUNPIC_HOME:-${XDG_DATA_HOME:-$HOME/.local/share}/$APP_NAME}"
SERVICE_DIR="${XDG_CONFIG_HOME:-$HOME/.config}/systemd/user"
SERVICE_FILE="$SERVICE_DIR/$SERVICE_NAME.service"
WANTS_LINK="$SERVICE_DIR/default.target.wants/$SERVICE_NAME.service"
DATA_DIR="${XDG_DATA_HOME:-$HOME/.local/share}/$APP_NAME"
CONFIG_DIR="${XDG_CONFIG_HOME:-$HOME/.config}/$APP_NAME"
CACHE_DIR="${XDG_CACHE_HOME:-$HOME/.cache}/$APP_NAME"
RUNTIME_DIR="${XDG_RUNTIME_DIR:-${TMPDIR:-/tmp}}/$APP_NAME"
PURGE=false

usage() {
    cat <<EOF
Usage: $0 [--purge]

Uninstall the $APP_NAME user service.

Options:
  --purge   Also remove user data, config, cache, and runtime directories.
  -h, --help
            Show this help.

Environment:
  SERVICE_NAME  systemd user service name (default: mikunpic)
  JAR_NAME      installed jar name (default: mikunpic.jar)
  MIKUNPIC_HOME installed jar directory (default: XDG_DATA_HOME/mikunpic)
EOF
}

while [[ $# -gt 0 ]]; do
    case "$1" in
        --purge)
            PURGE=true
            ;;
        -h|--help)
            usage
            exit 0
            ;;
        *)
            echo "Unknown option: $1" >&2
            usage >&2
            exit 1
            ;;
    esac
    shift
done

if [[ "$(id -u)" -eq 0 ]]; then
    echo "User uninstall should not be run as root." >&2
    exit 1
fi

remove_empty_dir() {
    local dir="$1"

    if [[ -d "$dir" ]]; then
        rmdir "$dir" 2>/dev/null || true
    fi
}

purge_dir() {
    local dir="$1"

    [[ -e "$dir" ]] || return 0

    if [[ -z "$dir" || "$dir" == "/" || "$dir" == "$HOME" ]]; then
        echo "Refusing to purge unsafe path: $dir" >&2
        return 1
    fi

    if [[ "$(basename -- "$dir")" != "$APP_NAME" ]]; then
        echo "Refusing to purge $dir because its basename is not '$APP_NAME'." >&2
        echo "Remove that custom directory manually if it is no longer needed." >&2
        return 1
    fi

    rm -rf -- "$dir"
}

if command -v systemctl >/dev/null 2>&1; then
    systemctl --user disable --now "$SERVICE_NAME.service" >/dev/null 2>&1 || true
fi

rm -f -- "$SERVICE_FILE"
rm -f -- "$WANTS_LINK"

if command -v systemctl >/dev/null 2>&1; then
    systemctl --user daemon-reload >/dev/null 2>&1 || true
    systemctl --user reset-failed "$SERVICE_NAME.service" >/dev/null 2>&1 || true
fi

rm -f -- "$WORKING_DIR/$JAR_NAME"

if [[ "$PURGE" == true ]]; then
    purge_dir "$RUNTIME_DIR"
    purge_dir "$CACHE_DIR"
    purge_dir "$CONFIG_DIR"
    purge_dir "$DATA_DIR"

    if [[ "$WORKING_DIR" != "$DATA_DIR" ]]; then
        purge_dir "$WORKING_DIR"
    fi
else
    remove_empty_dir "$WORKING_DIR"
fi

echo "Uninstalled $SERVICE_NAME user service"
echo "Removed service file: $SERVICE_FILE"
echo "Removed jar: $WORKING_DIR/$JAR_NAME"

if [[ "$PURGE" == true ]]; then
    echo "Purged user data, config, cache, and runtime directories"
else
    echo "User data/config/cache were kept. Run with --purge to remove them."
fi

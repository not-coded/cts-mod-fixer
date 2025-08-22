# Combat Test 8c Mod Fixer

Fixes mod compatibility issues for Combat Test 8c

[Download](https://github.com/not-coded/cts-mod-fixer/releases/download/1.0.0/modfixer-1.0.0.jar)

## Fixes

- Fabric API (<https://modrinth.com/mod/fabric-api/version/0.42.0+1.16>)
- Mod Menu (<https://modrinth.com/mod/modmenu/version/1.16.23>)
- Iris (<https://modrinth.com/mod/iris/version/1.4.5+1.16.5>)

## Manual Fixes

Add this to your `.minecraft/config/fabric_loader_dependencies.json`

```json
{
  "version": 1,
  "overrides": {
    "iris": {
      "depends": {
        "minecraft": "1.16.x"
      }
    }
  }
}
```

This is to fix the **"Incompatable mods found!"** error.

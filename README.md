# Enchant Popoff

Displays enchantment tooltips underneath the item name popup

## Configuration

### Default Configuration

```json
{
  "defaultColor": "AQUA",
  "colorOverrides": {},
  "maxLevelFormat": "ITALIC",
  "mergeLines": true
}
```

### Changing Things

#### `defaultColor`

The default color to use for all enchantments. See below for all valid values.

#### `colorOverrides`

This is a `String` -> `String` map that allows you to set a specific color for a given enchantment.

Example:

```json
{
  "colorOverrides": {
    "minecraft:sharpness": "RED"
  }
}
```

This will make the sharpness text be red. See below for all valid values.

#### `maxLevelFormat`

This is a text format that will be applied to enchantment text when it is max level. See below for all valid values.

#### `mergeLines`

When true, this will merge lines until they are as wide as `scaledScreenWidth / 2`. When false, every enchantment will be on it's own line.

### Text formats

#### Colors

Valid color values are as follows:
`BLACK`, `DARK_BLUE`, `DARK_GREEN`, `DARK_AQUA`, `DARK_RED`, `DARK_PURPLE`, `GOLD`, `GRAY`, `DARK_GRAY`, `BLUE`, `GREEN`, `AQUA`, `RED`, `LIGHT_PURPLE`, `YELLOW`, `WHITE`

#### Decorators

Valid decorator values are as follows:
`OBFUSCATED`, `BOLD`, `STRIKETHROUGH`, `UNDERLINE`, `ITALIC`, `RESET`

`RESET` will be treated as `NONE` and use whatever the `colorOverrides` map provides as usual.
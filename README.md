# Pick Up Notifier

A Minecraft mod. Downloads can be found on CurseForge.

![](https://raw.githubusercontent.com/Fuzss/modresources/main/pages/data/pickupnotifier/banner.png)

## Configuration
Apart from all the display settings found in the `.minecraft/config/pickupnotifier/pickupnotifier-client.toml` file, Pick Up Notifier also supports blacklisting or whitelisting individual items from showing up via separate `.json` based configuration files.

Just as with the client config, all those files need to be put in `.minecraft/config/pickupnotifier/`. File names do not matter, name your custom configs however you like as long as the extension remains as `.json`.

Those configs have three fields you can configure:

| Key         | Type                 | Description                                                                                                                                                               | Mandatory | Default |
|-------------|----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------|---------|
| `inverted`  | `boolean`            | Turns this whole file into a whitelist instead of blacklist.                                                                                                              | `false`   | `false` |
| `dimension` | `ResourceLocation`   | Id of the dimension for this config to apply in. If left blank or absent this will be the general config for all dimension that do not already have their own config.     | `false`   | `""`    |
| `items`     | `ResourceLocation[]` | The items to be included in the blacklist/whitelist. Format for every entry is `<namespace>:<path>`. Path may use asterisk as wildcard parameter. Tags are not supported. | `false`   | `[]`    |

### Examples

The config file below will show prevent stone, diorite, andesite and granite from showing in the pickup log in all dimensions that do not provide their own config. All other items are shown when picked up.

So considering the two additional examples below, this behavior would not apply to the nether and end dimensions since dimensions with their own config file do not inherit behavior from the general config.

```json
{
    "items": [
        "minecraft:stone",
        "minecraft:diorite",
        "minecraft:andesite",
        "minecraft:granite"
    ]
}
```

The config file below will only show netherrack, soul sand, soul soil, crimson nylium and warped nylium in the pickup log when in the nether dimension.

```json
{
    "inverted": true,
    "dimension": "minecraft:the_nether",
    "items": [
        "minecraft:netherrack",
        "minecraft:soul_*",
        "minecraft:*_nylium"
    ]
}
```

The config file below will prevent all pickups from being logged when in the end dimension.

```json
{
    "inverted": true,
    "dimension": "minecraft:the_end"
}
```

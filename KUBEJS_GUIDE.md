# SteamCore KubeJS Integration

This document describes how to use SteamCore's KubeJS integration to customize the behavior of the Disassembly Table and Engineering Table.

## Events

### Disassembly Event

Fired when an item is about to be disassembled in the Disassembly Table.

**Event Type:** `DisassemblyEvent`

**Example Usage:**

```javascript
// In your KubeJS startup script or server script
SteamCoreEvents.disassembly(event => {
    let input = event.getInput();
    
    // Cancel default behavior and provide custom outputs
    if (input.id === 'minecraft:diamond_sword') {
        event.setCustomOutputs([
            Item.of('minecraft:diamond', 2),
            Item.of('minecraft:stick', 1),
            Item.of('steamcore:forgotten_essence', 1)
        ]);
    }
    
    // Or simply cancel the disassembly
    if (input.id === 'minecraft:netherite_sword') {
        event.cancel();
    }
});
```

**Available Methods:**
- `getLevel()` - Get the world/level
- `getInput()` - Get the input ItemStack being disassembled
- `isCancelled()` - Check if cancelled
- `cancel()` - Cancel the default disassembly logic
- `setCustomOutputs(ItemStack...)` - Set custom outputs (also cancels default)
- `getCustomOutputs()` - Get custom outputs if set
- `hasCustomOutputs()` - Check if custom outputs were set

---

### Engineering Table Event

Fired when goggles are being attached to or detached from a helmet in the Engineering Table.

**Event Type:** `EngineeringTableEvent`

**Example Usage:**

```javascript
// In your KubeJS startup script or server script
SteamCoreEvents.engineeringTable(event => {
    let helmet = event.getHelmet();
    let goggles = event.getGoggles();
    
    // Only allow attaching goggles to diamond helmets
    if (event.isAttach() && helmet.id !== 'minecraft:diamond_helmet') {
        event.cancel('Only diamond helmets can have goggles attached!');
    }
    
    // Prevent detaching goggles from netherite helmets
    if (event.isDetach() && helmet.id === 'minecraft:netherite_helmet') {
        event.cancel('Cannot remove goggles from netherite helmets!');
    }
});
```

**Available Methods:**
- `getLevel()` - Get the world/level
- `getHelmet()` - Get the helmet ItemStack
- `getGoggles()` - Get the goggles ItemStack (may be empty for detach)
- `getType()` - Get operation type (ATTACH or DETACH)
- `isAttach()` - Check if this is an attach operation
- `isDetach()` - Check if this is a detach operation
- `isCancelled()` - Check if cancelled
- `cancel()` - Cancel the operation
- `cancel(String message)` - Cancel with a message
- `getCancelMessage()` - Get the cancellation message

---

## Installation

1. Install KubeJS mod alongside SteamCore
2. Create a script file in `kubejs/startup_scripts/` or `kubejs/server_scripts/`
3. Use the event bindings as shown above

## Notes

- The Disassembly Event allows you to override the default disassembly logic completely
- The Engineering Table Event allows you to add restrictions or requirements
- Events are fired on the server side
- Custom outputs must fit in the output slots (max 6 items for Disassembly Table)

## Example: Custom Disassembly Recipes

```javascript
// Give more essence for modded relics
SteamCoreEvents.disassembly(event => {
    let input = event.getInput();
    
    // Check if item is from a specific mod
    if (input.id.startsWith('artifacts:')) {
        event.setCustomOutputs([
            Item.of('steamcore:forgotten_essence', 5),
            Item.of('minecraft:diamond', 1)
        ]);
    }
    
    // Prevent disassembling quest items
    if (input.hasTag('quest_item')) {
        event.cancel();
    }
});
```

## Example: Engineering Table Restrictions

```javascript
// Only allow specific helmet types
const ALLOWED_HELMETS = [
    'minecraft:diamond_helmet',
    'minecraft:netherite_helmet',
    'create:copper_backtank' // if you want to support backtanks
];

SteamCoreEvents.engineeringTable(event => {
    if (event.isAttach()) {
        let helmetId = event.getHelmet().id;
        if (!ALLOWED_HELMETS.includes(helmetId)) {
            event.cancel('This helmet type cannot have goggles attached!');
        }
    }
});
```

---

**Last Updated:** 2026-07-14
**SteamCore Version:** 1.1.4b+
**Minecraft Version:** 1.21.1

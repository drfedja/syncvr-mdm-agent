# SyncVR MDM Agent assessment task

**MDM Agent** is an Android module providing policy delivery for the Connectivity Lab app.  

## Features Completed

- Reads local policy JSON (`/sdcard/Download/mdm_policy.json`).  
- Persists last applied policy with content hash.  
- Broadcasts effective configuration to `Connectivity Lab` via `PolicyReceiver`.  
- Handles parsing failures gracefully, keeping previously valid policy.  
- WorkManager task set up for periodic policy broadcasting.  

## Usage

1. Place `mdm_policy.json` in `/sdcard/Download/`.  
2. MDM Agent will read and broadcast updates automatically.  
3. Connectivity Lab will apply updates at runtime.

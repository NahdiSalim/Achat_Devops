# Fix Nexus Repository Configuration

## The Problem

Your repository might be rejecting deployments. Let's fix the settings.

## Steps to Fix (3 minutes)

### 1. Sign In to Nexus

1. Go to: http://localhost:8081
2. Sign in:
   - Username: `admin`
   - Password: `admin123`

### 2. Go to Repository Settings

1. Click ⚙️ **Settings** (gear icon at top)
2. In left menu: **Repository** → **Repositories**
3. Find and click: **`achat-releases`**

### 3. Check These Settings

Scroll down and verify:

- **Deployment policy:** Should be **"Allow redeploy"** ✅
  - If it says "Disable redeploy" or "Read-only", change it to **"Allow redeploy"**
  
- **Hosted** → **Type:** Should be **"hosted"** ✅
  - NOT "proxy" or "group"

### 4. Save Changes

- Scroll to bottom
- Click **"Save"** if you made any changes

### 5. Repeat for Snapshots

Do the same for **`achat-snapshots`** repository:
- Deployment policy: **"Allow redeploy"**
- Type: **"hosted"**

## ✅ That's It!

Your repositories should now accept uploads.

## If Repository Doesn't Exist

If you don't see `achat-releases` or `achat-snapshots`:

### Create Release Repository:

1. Click **"Create repository"**
2. Choose: **"maven2 (hosted)"**
3. Settings:
   - **Name:** `achat-releases`
   - **Version policy:** `Release`
   - **Layout policy:** `Strict`
   - **Deployment policy:** **`Allow redeploy`** ⭐ IMPORTANT!
   - **Blob store:** `default`
4. Click **"Create repository"**

### Create Snapshot Repository:

1. Click **"Create repository"**
2. Choose: **"maven2 (hosted)"**
3. Settings:
   - **Name:** `achat-snapshots`
   - **Version policy:** `Snapshot`
   - **Layout policy:** `Strict`
   - **Deployment policy:** **`Allow redeploy`** ⭐ IMPORTANT!
   - **Blob store:** `default`
4. Click **"Create repository"**

## Test

After fixing:
1. Go to Jenkins
2. Run your pipeline
3. Watch the Nexus stage succeed! 🎉


# Fix JaCoCo Plugin Error

## The Problem
Your pipeline failed with:
```
java.lang.NoSuchMethodError: No such DSL method 'jacoco' found
```

## The Solution
Install the JaCoCo plugin in Jenkins.

## Steps to Fix (2 minutes)

### 1. Go to Jenkins Plugin Manager
1. Open Jenkins: http://localhost:8080
2. Click **"Manage Jenkins"** (left sidebar)
3. Click **"Plugins"**
4. Click **"Available plugins"** tab

### 2. Install JaCoCo Plugin
1. In the search box, type: **`JaCoCo`**
2. Find: **"JaCoCo plugin"**
3. Check the box next to it
4. Click **"Install"** (at the bottom)
5. ✅ Check: **"Restart Jenkins when installation is complete and no jobs are running"**

### 3. Wait for Restart
- Jenkins will install the plugin and restart (1-2 minutes)
- You'll be logged out - that's normal!
- Wait 1 minute, then refresh the page
- Log back in

### 4. Run Pipeline Again
1. Go to your pipeline: **Achat-DevOps-Pipeline** (or Achat_pipeline)
2. Click **"Build Now"**
3. Watch it succeed! ✅

## Expected Result

After installing the plugin, the pipeline will:
- ✅ Run 90 tests (all passing)
- ✅ Generate JaCoCo coverage report
- ✅ Publish coverage to Jenkins
- ✅ Continue to next stages

## That's It!

This is a common first-time setup issue. Once the plugin is installed, it will work forever!


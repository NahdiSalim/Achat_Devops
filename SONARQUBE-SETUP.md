# 📊 SonarQube Complete Setup Guide

## Step-by-Step Configuration (10 minutes)

---

## ✅ Step 1: Wait for SonarQube to Start (2 minutes)

SonarQube takes **90-120 seconds** to fully start after cleaning the data.

### Check if it's ready:
```bash
# Check SonarQube logs
docker logs achat-sonarqube --tail 20

# Look for this line:
# "SonarQube is operational"
```

### Or simply wait 2 minutes and try: http://localhost:9000

If you see the login page, you're ready! ✅

---

## ✅ Step 2: First Login (1 minute)

1. **Open SonarQube:** http://localhost:9000
2. Click **"Log in"** (top right)
3. **Default credentials:**
   - Username: `admin`
   - Password: `admin`
4. Click **"Log in"**

---

## ✅ Step 3: Change Password (1 minute)

**IMPORTANT:** SonarQube will force you to change the password!

1. You'll see: **"Please change your password"**
2. **Old password:** `admin`
3. **New password:** `admin123` (or your choice)
4. **Confirm password:** `admin123`
5. Click **"Update"**

✅ **Remember this password! You'll need it later.**

---

## ✅ Step 4: Create a Project (2 minutes)

### Option A: Manual Setup (Recommended for Learning)

1. Click **"Create a project"** (blue button)
   - Or go to: **Projects** → **"Create Project"**

2. Choose: **"Manually"**

3. **Fill in project details:**
   - **Project display name:** `Achat DevOps Project`
   - **Project key:** `achat-project`
   - **Main branch name:** `main`

4. Click **"Next"**

5. **Choose baseline:** Select **"Use the global setting"**

6. Click **"Create project"**

✅ **Project created!**

---

## ✅ Step 5: Generate Token for Jenkins (2 minutes)

This is the **MOST IMPORTANT** step!

### Generate Token:

1. After creating the project, you'll see: **"How do you want to analyze your repository?"**

2. Choose: **"With Jenkins"**

3. Choose: **"Maven"**

4. You'll see: **"Create a Jenkins configuration"**

5. Click on **"Generate a token"**
   - **Token name:** `jenkins-token`
   - Click **"Generate"**

6. **⚠️ COPY THE TOKEN NOW!**
   ```
   Example: squ_1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r
   ```
   
7. **Save it in Notepad or somewhere safe!** 
   - You **CANNOT** see it again!
   - We need this for Jenkins!

### Alternative Method (If you missed the token):

1. Click on your avatar (top right) → **"My Account"**
2. Go to **"Security"** tab
3. Under **"Generate Tokens":**
   - **Name:** `jenkins-token`
   - **Type:** `Global Analysis Token`
   - **Expires in:** `No expiration`
   - Click **"Generate"**
4. **Copy the token immediately!**

---

## ✅ Step 6: Configure SonarQube in Jenkins (3 minutes)

Now we connect Jenkins to SonarQube.

### 6.1: Add Token to Jenkins

1. **Open Jenkins:** http://localhost:8080

2. Go to: **Manage Jenkins** → **Credentials**

3. Click on **(global)** domain

4. Click **"Add Credentials"**

5. **Fill in:**
   - **Kind:** `Secret text`
   - **Scope:** `Global`
   - **Secret:** `<PASTE YOUR SONARQUBE TOKEN HERE>`
   - **ID:** `sonar-token-jenkins`
   - **Description:** `SonarQube Token for Jenkins`

6. Click **"Create"**

✅ **Token saved in Jenkins!**

### 6.2: Configure SonarQube Server in Jenkins

1. Still in Jenkins, go to: **Manage Jenkins** → **System**

2. Scroll down to: **"SonarQube servers"**

3. Click **"Add SonarQube"**

4. **Fill in:**
   - ✅ **Check:** `Environment variables → Enable injection of SonarQube server configuration`
   - **Name:** `SonarQube-Server`
   - **Server URL:** `http://achat-sonarqube:9000`
   - **Server authentication token:** Select `sonar-token-jenkins` (from dropdown)

5. Click **"Save"**

✅ **SonarQube connected to Jenkins!**

---

## ✅ Step 7: Configure SonarQube Scanner in Jenkins (2 minutes)

1. In Jenkins, go to: **Manage Jenkins** → **Tools**

2. Scroll to: **"SonarQube Scanner installations"**

3. Click **"Add SonarQube Scanner"**

4. **Fill in:**
   - **Name:** `SonarScanner`
   - ✅ **Check:** `Install automatically`
   - **Version:** Select `SonarQube Scanner 5.0.1.3006` (or latest)

5. Scroll to bottom and click **"Save"**

✅ **Scanner configured!**

---

## ✅ Step 8: Test the Connection (1 minute)

### Quick Test from Command Line:

```bash
# From your project directory
cd C:\Users\Lord\Documents\GitHub\Achat_Devops

# Test SonarQube is accessible
curl http://localhost:9000/api/system/health
```

**Expected response:**
```json
{"health":"GREEN","causes":[]}
```

If you see this, SonarQube is working! ✅

---

## ✅ Step 9: Run Jenkins Pipeline (Final Test!)

1. Go to your pipeline in Jenkins: **Achat-DevOps-Pipeline** (or Achat_pipeline)

2. Click **"Build Now"**

3. Watch the stages:
   - ✅ Tests should pass
   - ✅ JaCoCo report (if plugin installed)
   - ✅ **SonarQube Analysis** ← This should work now!
   - ✅ **Quality Gate** ← This will run!

4. After build completes, check SonarQube:
   - Go to: http://localhost:9000
   - Click on **"Projects"**
   - You should see: **"Achat DevOps Project"** with analysis data!

---

## 🎉 Success Indicators

You'll know it's working when:

### In SonarQube Dashboard:
- ✅ Project visible: "Achat DevOps Project"
- ✅ Lines of code: ~1000+
- ✅ Code coverage: 70%+
- ✅ Quality gate: PASSED (green)
- ✅ Issues: Some bugs/code smells shown
- ✅ Last analysis: "a few seconds ago"

### In Jenkins Console:
```
[INFO] ANALYSIS SUCCESSFUL
[INFO] Note that you will be able to access the updated dashboard once the server has processed the submitted analysis report
[INFO] More about the report processing at http://achat-sonarqube:9000/api/ce/task?id=AY...
✅ SonarQube analysis completed!
```

---

## 📊 Understanding Your SonarQube Dashboard

After first analysis, you'll see:

### Overview Tab:
- **Quality Gate Status:** PASSED ✅
- **Bugs:** ~5-10
- **Vulnerabilities:** 0
- **Code Smells:** ~50-100
- **Coverage:** 70-80%
- **Duplications:** <5%

### Issues Tab:
- List of all code issues found
- Categorized by severity: Blocker, Critical, Major, Minor, Info

### Measures Tab:
- Detailed metrics
- Technical debt
- Complexity
- Size metrics

---

## 🔧 Troubleshooting

### Issue 1: SonarQube won't start (keeps restarting)

**Check logs:**
```bash
docker logs achat-sonarqube --tail 50
```

**Solution:** We already fixed this by cleaning volumes! Just wait 2 minutes.

---

### Issue 2: "SonarQube is offline" message

**Wait 90-120 seconds** after starting. Check:
```bash
docker logs achat-sonarqube | findstr "operational"
```

Look for: `SonarQube is operational`

---

### Issue 3: Jenkins can't connect to SonarQube

**Check these:**
1. ✅ SonarQube URL in Jenkins: `http://achat-sonarqube:9000` (use container name!)
2. ✅ Token is correct (regenerate if needed)
3. ✅ Credential ID: `sonar-token-jenkins` (exact match!)
4. ✅ Both containers in same network:
   ```bash
   docker network inspect achat-network
   ```

---

### Issue 4: Quality Gate times out in Jenkins

**This is NORMAL on first run!** 

The quality gate needs a webhook to work properly. For now:
- Pipeline shows warning but continues
- Check results manually in SonarQube dashboard

**To fix (optional):**
1. In SonarQube: **Administration** → **Configuration** → **Webhooks**
2. Create webhook:
   - **Name:** `Jenkins`
   - **URL:** `http://achat-jenkins:8080/sonarqube-webhook/`
   - Click **"Create"**

---

### Issue 5: Token expired or invalid

**Regenerate token:**
1. SonarQube → Your avatar → **My Account** → **Security**
2. Revoke old token
3. Generate new token: `jenkins-token-new`
4. Update in Jenkins credentials

---

## 📋 Quick Checklist

Use this to verify everything is configured:

- [ ] SonarQube accessible at http://localhost:9000
- [ ] Logged in with admin/admin123
- [ ] Project created: "achat-project"
- [ ] Token generated and saved
- [ ] Token added to Jenkins credentials (ID: sonar-token-jenkins)
- [ ] SonarQube server configured in Jenkins
- [ ] SonarQube Scanner installed in Jenkins
- [ ] Jenkins pipeline runs successfully
- [ ] Analysis results visible in SonarQube dashboard

---

## 🎯 What's Next?

After SonarQube is working:

1. **Review code quality issues**
   - Check bugs and vulnerabilities
   - Read code smells
   - Plan improvements

2. **Improve coverage**
   - Add more tests
   - Target: 80%+ coverage

3. **Setup quality gates**
   - Define custom quality profiles
   - Set coverage thresholds

4. **Move to Nexus setup**
   - Continue with pipeline configuration

---

## 💡 Pro Tips

1. **Bookmark SonarQube:** http://localhost:9000
2. **Check analysis after each build:** See improvements over time
3. **Fix critical issues first:** Start with bugs and vulnerabilities
4. **Use SonarLint in IDE:** Real-time code quality feedback
5. **Regular cleanup:** Review and fix code smells weekly

---

## ⚡ Quick Commands Reference

```bash
# Check if SonarQube is ready
docker logs achat-sonarqube --tail 20

# Check SonarQube status
curl http://localhost:9000/api/system/health

# Restart SonarQube if needed
docker-compose restart sonarqube

# View SonarQube logs in real-time
docker logs achat-sonarqube -f

# Clean and restart (if problems)
docker-compose stop sonarqube sonarqube-db
docker volume rm achat_devops_sonarqube_data achat_devops_postgresql_data
docker-compose up -d sonarqube-db sonarqube
```

---

**Ready? Start with Step 1! 🚀**

**Time needed: 10-15 minutes total**


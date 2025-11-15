# 📊 Prometheus & Grafana Monitoring Setup - Step by Step

## Overview
Set up complete monitoring for your application with Prometheus and Grafana.

---

## What You'll Monitor

- **Application Metrics**: Request count, response times, errors
- **JVM Metrics**: Memory, threads, garbage collection
- **System Metrics**: CPU, memory, disk usage
- **Custom Metrics**: Business metrics from your app

---

## Step 1: Verify Prometheus is Running

Your `docker-compose.yml` already has Prometheus!

```bash
# Check Prometheus container
docker ps | grep prometheus

# View Prometheus logs
docker logs achat-prometheus

# Access Prometheus UI
```
Open: **http://localhost:9090**

---

## Step 2: Verify Grafana is Running

```bash
# Check Grafana container
docker ps | grep grafana

# View Grafana logs
docker logs achat-grafana

# Access Grafana UI
```
Open: **http://localhost:3000**

**Login:**
- Username: `admin`
- Password: `admin`
- (Change password when prompted)

---

## Step 3: Configure Prometheus Data Source in Grafana

### Manual Setup:
1. Open Grafana: http://localhost:3000
2. Login (admin/admin)
3. Go to **Configuration** (⚙️) → **Data Sources**
4. Click **Add data source**
5. Select **Prometheus**
6. Configure:
   - Name: `Prometheus`
   - URL: `http://prometheus:9090`
   - Access: `Server (default)`
7. Click **Save & Test**
8. You should see: ✅ **Data source is working**

---

## Step 4: Update Prometheus Configuration

Your `prometheus.yml` should already be configured. Let's verify:

```bash
cat prometheus.yml
```

### It should have:
```yaml
scrape_configs:
  - job_name: 'achat-app'
    metrics_path: '/SpringMVC/actuator/prometheus'
    static_configs:
      - targets: ['app:8089']
```

### If you need to update it, restart Prometheus:
```bash
docker-compose restart prometheus
```

---

## Step 5: Verify Application Metrics

### Check if your app is exposing metrics:

```bash
# From your host
curl http://localhost:8089/SpringMVC/actuator/prometheus

# You should see metrics like:
# jvm_memory_used_bytes
# http_server_requests_seconds_count
# process_cpu_usage
```

### Check in Prometheus UI:
1. Go to http://localhost:9090
2. Go to **Status** → **Targets**
3. Find `achat-app` job
4. Status should be **UP** ✅

---

## Step 6: Test Prometheus Queries

In Prometheus UI (http://localhost:9090):

### Try these queries:

```promql
# HTTP request rate
rate(http_server_requests_seconds_count[5m])

# JVM memory usage
jvm_memory_used_bytes{area="heap"}

# CPU usage
process_cpu_usage

# Active threads
jvm_threads_live_threads

# HTTP request duration (95th percentile)
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))
```

Click **Execute** and view graphs!

---

## Step 7: Import Grafana Dashboards

### Option A: Import Pre-built Dashboards

1. In Grafana, click **+** → **Import**
2. Enter Dashboard ID: `4701` (JVM Micrometer)
3. Click **Load**
4. Select Prometheus data source
5. Click **Import**

### Other useful dashboards:
- **11378** - Spring Boot 2.1 Statistics
- **12900** - Spring Boot Statistics  
- **6756** - Spring Boot 2.x Statistics

---

## Step 8: Create Custom Dashboard

### Create Application Dashboard:

1. Click **+** → **Create** → **Dashboard**
2. Click **Add new panel**

### Panel 1: HTTP Request Rate
```promql
sum(rate(http_server_requests_seconds_count[5m])) by (uri)
```
- Title: "Request Rate"
- Visualization: Graph

### Panel 2: Response Time (p95)
```promql
histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket[5m])) by (le, uri))
```
- Title: "Response Time (95th percentile)"
- Unit: seconds

### Panel 3: Error Rate
```promql
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m]))
```
- Title: "Error Rate (5xx)"
- Color: Red

### Panel 4: JVM Memory
```promql
jvm_memory_used_bytes{area="heap"}
```
- Title: "Heap Memory Usage"
- Unit: bytes

### Panel 5: Active Threads
```promql
jvm_threads_live_threads
```
- Title: "Active Threads"

### Panel 6: CPU Usage
```promql
process_cpu_usage
```
- Title: "CPU Usage"
- Unit: percent (0-1)

Click **Save dashboard** (💾 icon top right)

---

## Step 9: Configure Alerts (Optional)

### Create Alert in Grafana:

1. Open a panel (e.g., Error Rate)
2. Click panel title → **Edit**
3. Go to **Alert** tab
4. Click **Create Alert**

### Example Alert: High Error Rate
```
Condition:
WHEN avg() OF query(A, 5m, now) IS ABOVE 10

Message:
High error rate detected! Errors: {{ $value }}
```

5. Configure notification channel (Email, Slack, etc.)
6. Save

---

## Step 10: Set Up Alert Notifications

### Configure Email Notifications:

1. Go to **Alerting** (🔔) → **Notification channels**
2. Click **Add channel**
3. Configure:
   - Name: `Email Alerts`
   - Type: **Email**
   - Addresses: `your-email@example.com`
4. Click **Send Test**
5. Save

### Configure Slack (Optional):

1. Create Slack Webhook URL
2. Add channel in Grafana
3. Type: **Slack**
4. URL: Your webhook URL
5. Test and Save

---

## Step 11: Monitor Kubernetes (If Using K8s)

### Add Kubernetes Monitoring:

1. Update `prometheus.yml`:
```yaml
scrape_configs:
  - job_name: 'kubernetes-pods'
    kubernetes_sd_configs:
      - role: pod
    relabel_configs:
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_scrape]
        action: keep
        regex: true
```

2. Import K8s dashboards in Grafana:
   - **3119** - Kubernetes cluster monitoring
   - **7249** - Kubernetes Cluster
   - **8588** - Kubernetes Deployment Statistics

---

## Step 12: Export and Backup Dashboards

### Export Dashboard:
1. Open dashboard
2. Click **⚙️** (Dashboard settings)
3. Click **JSON Model**
4. Copy JSON
5. Save to file: `grafana/dashboards/achat-dashboard.json`

### Backup:
```bash
# Backup Grafana data
docker exec achat-grafana tar czf /tmp/grafana-backup.tar.gz /var/lib/grafana
docker cp achat-grafana:/tmp/grafana-backup.tar.gz ./grafana-backup.tar.gz
```

---

## Step 13: Provision Dashboards Automatically

Create `grafana/provisioning/dashboards/dashboard.yml`:

```yaml
apiVersion: 1

providers:
  - name: 'Achat Dashboards'
    orgId: 1
    folder: ''
    type: file
    disableDeletion: false
    updateIntervalSeconds: 10
    allowUiUpdates: true
    options:
      path: /etc/grafana/provisioning/dashboards
```

Place dashboard JSON files in `grafana/provisioning/dashboards/`

Restart Grafana:
```bash
docker-compose restart grafana
```

---

## Step 14: Jenkins Pipeline Monitoring

### Add Monitoring to Jenkinsfile:

The **CONFIGURE MONITORING** stage is already in your Jenkinsfile!

It will verify:
- Prometheus scraping
- Application metrics endpoints
- Health checks

---

## Useful Prometheus Queries

```promql
# Request rate by endpoint
sum(rate(http_server_requests_seconds_count[5m])) by (uri)

# Average response time
rate(http_server_requests_seconds_sum[5m]) / rate(http_server_requests_seconds_count[5m])

# Error rate
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m]))

# Success rate (percentage)
sum(rate(http_server_requests_seconds_count{status=~"2.."}[5m])) / sum(rate(http_server_requests_seconds_count[5m])) * 100

# JVM memory usage percentage
(jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}) * 100

# Garbage collection time
rate(jvm_gc_pause_seconds_sum[5m])

# Database connection pool
hikaricp_connections_active
hikaricp_connections_idle

# Tomcat threads
tomcat_threads_busy_threads
tomcat_threads_current_threads
```

---

## Common Issues and Solutions

### 🔴 Target is DOWN in Prometheus
**Check:**
```bash
# Is application running?
docker ps | grep achat-app

# Are actuator endpoints enabled?
curl http://localhost:8089/SpringMVC/actuator/health
curl http://localhost:8089/SpringMVC/actuator/prometheus

# Check prometheus config
docker exec achat-prometheus cat /etc/prometheus/prometheus.yml
```

### 🔴 No data in Grafana
**Fix:**
1. Verify Prometheus data source is configured
2. Check Prometheus has data (go to Prometheus UI)
3. Verify time range in Grafana (top right)
4. Try query in Prometheus first

### 🔴 Grafana login issues
**Reset admin password:**
```bash
docker exec -it achat-grafana grafana-cli admin reset-admin-password newpassword
```

---

## Monitoring Best Practices

✅ **Set up alerts for critical metrics**  
✅ **Monitor both technical and business metrics**  
✅ **Keep dashboards simple and focused**  
✅ **Use consistent time ranges**  
✅ **Document what each metric means**  
✅ **Set up notification channels**  
✅ **Review metrics regularly**  
✅ **Test alerts (false positives vs false negatives)**  

---

## Performance Tuning

### Optimize Prometheus:
```yaml
# prometheus.yml
global:
  scrape_interval: 15s  # How often to scrape
  evaluation_interval: 15s  # How often to evaluate rules
  scrape_timeout: 10s  # Timeout for scrapes
```

### Optimize Grafana:
- Reduce dashboard refresh rate
- Use recording rules for complex queries
- Limit time ranges on heavy dashboards

---

## Next Steps

✅ **Monitoring configured!**  

### Your Complete DevOps Pipeline Now Has:

✅ **Code Quality**: SonarQube  
✅ **Artifact Repository**: Nexus  
✅ **Containerization**: Docker  
✅ **Orchestration**: Kubernetes  
✅ **Infrastructure**: Terraform  
✅ **Monitoring**: Prometheus + Grafana  
✅ **CI/CD**: Jenkins  

### 🎉 Congratulations!

You now have a **complete enterprise DevOps pipeline**!

---

## Quick Reference URLs

- **Jenkins**: http://localhost:8080
- **SonarQube**: http://localhost:9000
- **Nexus**: http://localhost:8081
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000
- **Application**: http://localhost:8089/SpringMVC/
- **App Health**: http://localhost:8089/SpringMVC/actuator/health
- **App Metrics**: http://localhost:8089/SpringMVC/actuator/prometheus

---

## Additional Resources

- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [Micrometer Documentation](https://micrometer.io/docs)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)


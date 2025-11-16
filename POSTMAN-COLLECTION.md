# 📮 Postman Collection - Achat Application API

Complete CRUD operations for all entities in your Achat application.

**Base URL (Local):** `http://localhost:8089/SpringMVC`  
**Base URL (AWS):** `http://YOUR_AWS_IP/SpringMVC`

---

## 📋 Table of Contents

1. [Fournisseur (Supplier)](#-fournisseur-supplier)
2. [Produit (Product)](#-produit-product)
3. [Stock](#-stock)
4. [Operateur (Operator)](#-operateur-operator)
5. [Categorie Produit](#-categorie-produit)
6. [Secteur Activite](#-secteur-activite)

---

## 🏢 Fournisseur (Supplier)

### 1. GET All Fournisseurs
```
Method: GET
URL: http://localhost:8089/SpringMVC/fournisseur/retrieve-all-fournisseurs
```

**Response:**
```json
[
  {
    "idFournisseur": 1,
    "code": "F001",
    "libelle": "Tech Supplies Inc",
    "categorieFournisseur": "ORDINAIRE"
  }
]
```

---

### 2. GET Fournisseur by ID
```
Method: GET
URL: http://localhost:8089/SpringMVC/fournisseur/retrieve-fournisseur/1
```

**Response:**
```json
{
  "idFournisseur": 1,
  "code": "F001",
  "libelle": "Tech Supplies Inc",
  "categorieFournisseur": "ORDINAIRE"
}
```

---

### 3. POST Add Fournisseur ⭐
```
Method: POST
URL: http://localhost:8089/SpringMVC/fournisseur/add-fournisseur
Headers: Content-Type: application/json
```

**Body (JSON):**
```json
{
  "code": "F001",
  "libelle": "Tech Supplies Inc",
  "categorieFournisseur": "ORDINAIRE"
}
```

**Alternative:**
```json
{
  "code": "F002",
  "libelle": "Premium Electronics",
  "categorieFournisseur": "CONVENTIONNE"
}
```

**Response:**
```json
{
  "idFournisseur": 1,
  "code": "F001",
  "libelle": "Tech Supplies Inc",
  "categorieFournisseur": "ORDINAIRE"
}
```

---

### 4. PUT Update Fournisseur
```
Method: PUT
URL: http://localhost:8089/SpringMVC/fournisseur/modify-fournisseur
Headers: Content-Type: application/json
```

**Body (JSON):**
```json
{
  "idFournisseur": 1,
  "code": "F001-UPDATED",
  "libelle": "Tech Supplies International",
  "categorieFournisseur": "CONVENTIONNE"
}
```

---

### 5. DELETE Fournisseur
```
Method: DELETE
URL: http://localhost:8089/SpringMVC/fournisseur/remove-fournisseur/1
```

**Response:** 200 OK (no body)

---

## 📦 Produit (Product)

### 1. GET All Produits
```
Method: GET
URL: http://localhost:8089/SpringMVC/produit/retrieve-all-produits
```

---

### 2. GET Produit by ID
```
Method: GET
URL: http://localhost:8089/SpringMVC/produit/retrieve-produit/1
```

---

### 3. POST Add Produit ⭐
```
Method: POST
URL: http://localhost:8089/SpringMVC/produit/add-produit
Headers: Content-Type: application/json
```

**Body (JSON):**
```json
{
  "codeProduit": "P001",
  "libelleProduit": "Laptop Dell XPS 15",
  "prix": 1299.99,
  "dateCreation": "2024-01-15",
  "dateDerniereModification": "2024-01-15"
}
```

**More Examples:**
```json
{
  "codeProduit": "P002",
  "libelleProduit": "iPhone 15 Pro",
  "prix": 999.99,
  "dateCreation": "2024-01-15",
  "dateDerniereModification": "2024-01-15"
}
```

```json
{
  "codeProduit": "P003",
  "libelleProduit": "Samsung Galaxy S24",
  "prix": 899.99,
  "dateCreation": "2024-01-15",
  "dateDerniereModification": "2024-01-15"
}
```

---

### 4. PUT Update Produit
```
Method: PUT
URL: http://localhost:8089/SpringMVC/produit/modify-produit
Headers: Content-Type: application/json
```

**Body (JSON):**
```json
{
  "idProduit": 1,
  "codeProduit": "P001-UPD",
  "libelleProduit": "Laptop Dell XPS 15 - Updated",
  "prix": 1199.99,
  "dateCreation": "2024-01-15",
  "dateDerniereModification": "2024-11-16"
}
```

---

### 5. DELETE Produit
```
Method: DELETE
URL: http://localhost:8089/SpringMVC/produit/remove-produit/1
```

---

## 📊 Stock

### 1. GET All Stocks
```
Method: GET
URL: http://localhost:8089/SpringMVC/stock/retrieve-all-stocks
```

---

### 2. GET Stock by ID
```
Method: GET
URL: http://localhost:8089/SpringMVC/stock/retrieve-stock/1
```

---

### 3. POST Add Stock ⭐
```
Method: POST
URL: http://localhost:8089/SpringMVC/stock/add-stock
Headers: Content-Type: application/json
```

**Body (JSON):**
```json
{
  "libelleStock": "Warehouse A - Electronics",
  "qte": 150,
  "qteMin": 20
}
```

**More Examples:**
```json
{
  "libelleStock": "Warehouse B - Computers",
  "qte": 75,
  "qteMin": 10
}
```

```json
{
  "libelleStock": "Warehouse C - Mobile Devices",
  "qte": 200,
  "qteMin": 30
}
```

---

### 4. PUT Update Stock
```
Method: PUT
URL: http://localhost:8089/SpringMVC/stock/modify-stock
Headers: Content-Type: application/json
```

**Body (JSON):**
```json
{
  "idStock": 1,
  "libelleStock": "Warehouse A - Electronics Updated",
  "qte": 180,
  "qteMin": 25
}
```

---

### 5. DELETE Stock
```
Method: DELETE
URL: http://localhost:8089/SpringMVC/stock/remove-stock/1
```

---

## 👤 Operateur (Operator)

### 1. GET All Operateurs
```
Method: GET
URL: http://localhost:8089/SpringMVC/operateur/retrieve-all-operateurs
```

---

### 2. GET Operateur by ID
```
Method: GET
URL: http://localhost:8089/SpringMVC/operateur/retrieve-operateur/1
```

---

### 3. POST Add Operateur ⭐
```
Method: POST
URL: http://localhost:8089/SpringMVC/operateur/add-operateur
Headers: Content-Type: application/json
```

**Body (JSON):**
```json
{
  "nom": "Dupont",
  "prenom": "Jean",
  "password": "password123"
}
```

**More Examples:**
```json
{
  "nom": "Martin",
  "prenom": "Marie",
  "password": "securePass456"
}
```

```json
{
  "nom": "Bernard",
  "prenom": "Pierre",
  "password": "myPassword789"
}
```

---

### 4. PUT Update Operateur
```
Method: PUT
URL: http://localhost:8089/SpringMVC/operateur/modify-operateur
Headers: Content-Type: application/json
```

**Body (JSON):**
```json
{
  "idOperateur": 1,
  "nom": "Dupont",
  "prenom": "Jean-Claude",
  "password": "newPassword123"
}
```

---

### 5. DELETE Operateur
```
Method: DELETE
URL: http://localhost:8089/SpringMVC/operateur/remove-operateur/1
```

---

## 🏷️ Categorie Produit

### 1. GET All Categories
```
Method: GET
URL: http://localhost:8089/SpringMVC/categorieProduit/retrieve-all-categorieProduit
```

---

### 2. GET Categorie by ID
```
Method: GET
URL: http://localhost:8089/SpringMVC/categorieProduit/retrieve-categorieProduit/1
```

---

### 3. POST Add Categorie ⭐
```
Method: POST
URL: http://localhost:8089/SpringMVC/categorieProduit/add-categorieProduit
Headers: Content-Type: application/json
```

**Body (JSON):**
```json
{
  "codeCategorie": "CAT001",
  "libelleCategorie": "Electronics"
}
```

**More Examples:**
```json
{
  "codeCategorie": "CAT002",
  "libelleCategorie": "Computers"
}
```

```json
{
  "codeCategorie": "CAT003",
  "libelleCategorie": "Mobile Devices"
}
```

---

### 4. DELETE Categorie
```
Method: DELETE
URL: http://localhost:8089/SpringMVC/categorieProduit/remove-categorieProduit/1
```

---

## 🎯 Quick Test Sequence

**Test your API in this order:**

### 1. Create Stock First
```
POST http://localhost:8089/SpringMVC/stock/add-stock
{
  "libelleStock": "Main Warehouse",
  "qte": 100,
  "qteMin": 15
}
```

### 2. Create Categorie Produit
```
POST http://localhost:8089/SpringMVC/categorieProduit/add-categorieProduit
{
  "codeCategorie": "ELEC",
  "libelleCategorie": "Electronics"
}
```

### 3. Create Produit
```
POST http://localhost:8089/SpringMVC/produit/add-produit
{
  "codeProduit": "LAP001",
  "libelleProduit": "Dell Laptop",
  "prix": 899.99,
  "dateCreation": "2024-11-16",
  "dateDerniereModification": "2024-11-16"
}
```

### 4. Create Fournisseur
```
POST http://localhost:8089/SpringMVC/fournisseur/add-fournisseur
{
  "code": "FTECH",
  "libelle": "Tech Supplier",
  "categorieFournisseur": "ORDINAIRE"
}
```

### 5. Create Operateur
```
POST http://localhost:8089/SpringMVC/operateur/add-operateur
{
  "nom": "Admin",
  "prenom": "System",
  "password": "admin123"
}
```

### 6. Get All Data
```
GET http://localhost:8089/SpringMVC/stock/retrieve-all-stocks
GET http://localhost:8089/SpringMVC/produit/retrieve-all-produits
GET http://localhost:8089/SpringMVC/fournisseur/retrieve-all-fournisseurs
GET http://localhost:8089/SpringMVC/operateur/retrieve-all-operateurs
```

---

## 🔧 Postman Setup Tips

### 1. Create Environment Variables

In Postman, create an environment with these variables:

**Variable Name** | **Value**
--- | ---
`base_url` | `http://localhost:8089/SpringMVC`
`aws_url` | `http://YOUR_AWS_IP/SpringMVC`

Then use in requests: `{{base_url}}/fournisseur/retrieve-all-fournisseurs`

### 2. Save Responses

After creating entities, save the IDs from responses to use in UPDATE and DELETE operations.

### 3. Test Collections

Create separate collections for:
- **Local Testing** (using localhost)
- **AWS Testing** (using AWS IP)
- **CI/CD Testing** (automated tests)

---

## 📝 Important Notes

### Date Format
Use ISO format for dates: `"2024-11-16"` or `"2024-11-16T10:30:00"`

### Enum Values
**categorieFournisseur** can only be:
- `ORDINAIRE`
- `CONVENTIONNE`

### Required Fields
Most entities require:
- Code/CodeProduit
- Libelle/LibelleProduit
- Other entity-specific fields

### IDs
- IDs are auto-generated
- Don't include ID in POST requests
- Always include ID in PUT requests

---

## 🧪 Testing Checklist

- [ ] **Create** - POST request for each entity
- [ ] **Read All** - GET all entities
- [ ] **Read One** - GET specific entity by ID
- [ ] **Update** - PUT request with modified data
- [ ] **Delete** - DELETE request
- [ ] **Verify** - GET to confirm deletion

---

## 🚀 Quick Copy-Paste for Testing

### Test Fournisseur CRUD
```bash
# 1. Create
curl -X POST http://localhost:8089/SpringMVC/fournisseur/add-fournisseur \
  -H "Content-Type: application/json" \
  -d '{"code":"F001","libelle":"Test Supplier","categorieFournisseur":"ORDINAIRE"}'

# 2. Get All
curl http://localhost:8089/SpringMVC/fournisseur/retrieve-all-fournisseurs

# 3. Get One (replace 1 with actual ID)
curl http://localhost:8089/SpringMVC/fournisseur/retrieve-fournisseur/1

# 4. Update (replace 1 with actual ID)
curl -X PUT http://localhost:8089/SpringMVC/fournisseur/modify-fournisseur \
  -H "Content-Type: application/json" \
  -d '{"idFournisseur":1,"code":"F001-UPD","libelle":"Updated Supplier","categorieFournisseur":"CONVENTIONNE"}'

# 5. Delete (replace 1 with actual ID)
curl -X DELETE http://localhost:8089/SpringMVC/fournisseur/remove-fournisseur/1
```

---

**💡 Tip:** Start with simple entities (Stock, Categorie) before moving to complex ones (Produit, Fournisseur)!

**🎯 Goal:** Test all CRUD operations and verify your API works perfectly before AWS deployment!


### Endpoints
#### POST /api/cache/put
Writes the data to the key store

**Body Parameters**

| Name | Required | Type   |    Description      |
|------|----------|--------|---------------------|
| key  | required | string | Key for the entry   |
| value| required | buffer | Value for the entry |

### Get /api/cache/get
Gets the data from the cache

**Url Parameters**
| Name | Required | Type   |    Description      |
|------|----------|--------|---------------------|
| key  | required | string | Key for the entry   |

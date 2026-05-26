import boto3, urllib.request, json, base64
from nacl import encoding, public

# ============================================================
# CONFIGURA ESTOS VALORES
# ============================================================
GITHUB_TOKEN = "TU_PAT_AQUI"
REPO         = "franc004/prueba"

DB_URL      = "jdbc:postgresql://carpultec-db.cpcwtim5ekxg.us-east-1.rds.amazonaws.com:5432/carpultec"
DB_USER     = "appadmin"
DB_PASSWORD = "Carpultec2024!"

GOOGLE_MAPS_API_KEY = "TU_GOOGLE_MAPS_KEY_AQUI"
JWT_SECRET          = "carpultec-jwt-secret-2024-super-seguro"
MAIL_FROM           = "noreply@carpultec.com"
CORS_ORIGINS        = "http://localhost:3000"
# ============================================================

def encrypt(pub_key_str, secret_value):
    pub_key   = public.PublicKey(pub_key_str.encode("utf-8"), encoding.Base64Encoder())
    box       = public.SealedBox(pub_key)
    encrypted = box.encrypt(secret_value.encode("utf-8"))
    return base64.b64encode(encrypted).decode("utf-8")

def get_public_key():
    url = f"https://api.github.com/repos/{REPO}/actions/secrets/public-key"
    req = urllib.request.Request(url, headers={
        "Authorization": f"token {GITHUB_TOKEN}",
        "Accept": "application/vnd.github+json"
    })
    with urllib.request.urlopen(req) as r:
        return json.loads(r.read())

def set_secret(name, value, key_id, pub_key):
    encrypted = encrypt(pub_key, value)
    url  = f"https://api.github.com/repos/{REPO}/actions/secrets/{name}"
    data = json.dumps({"encrypted_value": encrypted, "key_id": key_id}).encode()
    req  = urllib.request.Request(url, data=data, method="PUT", headers={
        "Authorization": f"token {GITHUB_TOKEN}",
        "Accept":        "application/vnd.github+json",
        "Content-Type":  "application/json"
    })
    urllib.request.urlopen(req)
    print(f"  OK  {name}")

# Obtener credenciales AWS desde boto3 (CloudShell)
session = boto3.Session()
creds   = session.get_credentials().get_frozen_credentials()

pk = get_public_key()
k, ki = pk["key"], pk["key_id"]

print("\n--- Credenciales AWS ---")
set_secret("AWS_ACCESS_KEY_ID",     creds.access_key, ki, k)
set_secret("AWS_SECRET_ACCESS_KEY", creds.secret_key, ki, k)
set_secret("AWS_SESSION_TOKEN",     creds.token,      ki, k)

print("\n--- Configuracion de la app ---")
set_secret("DB_URL",              DB_URL,              ki, k)
set_secret("DB_USERNAME",         DB_USER,             ki, k)
set_secret("DB_PASSWORD",         DB_PASSWORD,         ki, k)
set_secret("GOOGLE_MAPS_API_KEY", GOOGLE_MAPS_API_KEY, ki, k)
set_secret("JWT_SECRET",          JWT_SECRET,          ki, k)
set_secret("MAIL_FROM",           MAIL_FROM,           ki, k)
set_secret("CORS_ORIGINS",        CORS_ORIGINS,        ki, k)

print("\nListo! Todos los secrets actualizados.")

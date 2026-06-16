from fastapi import FastAPI
from pydantic import BaseModel
import re
import uvicorn

app = FastAPI(title="ScamBust API")

class SmsRequest(BaseModel):
    sender: str
    message: str

class ScamResponse(BaseModel):
    verdict: str
    confidence: float
    risk_level: int

# Layer 1: Keyword-based filtering
SCAM_KEYWORDS = [
    "kyc", "electricity bill", "blocked", "lottery", "urgent", "account suspended", 
    "won", "prize", "winner", "click here", "claim", "bank account", "refund"
]

# Layer 2: Heuristic analysis
URL_REGEX = r"(https?://[^\s]+|www\.[^\s]+|[a-zA-Z0-9-]+\.[a-zA-Z]{2,}(?:/[^\s]*)?)"
URGENCY_WORDS = ["immediate", "action required", "within 24 hours", "final warning", "expire"]

@app.post("/analyze", response_model=ScamResponse)
async def analyze_sms(request: SmsRequest):
    message_lower = request.message.lower()
    
    score = 0
    max_score = 100
    
    # Check Layer 1
    matched_keywords = [kw for kw in SCAM_KEYWORDS if kw in message_lower]
    score += len(matched_keywords) * 15
    
    # Check Layer 2
    if re.search(URL_REGEX, message_lower):
        score += 30 # URLs in SMS from unknown senders are highly suspicious
        
    matched_urgency = [uw for uw in URGENCY_WORDS if uw in message_lower]
    score += len(matched_urgency) * 20
    
    # Calculate Verdict
    risk_level = min(score // 20, 5) # 0 to 5 scale
    confidence = min(score / max_score, 0.99)
    
    if score >= 50:
        verdict = "SCAM"
    elif score >= 20:
        verdict = "SUSPICIOUS"
    else:
        verdict = "SAFE"
        
    return ScamResponse(
        verdict=verdict,
        confidence=confidence,
        risk_level=risk_level
    )

if __name__ == "__main__":
    # Run the server on all interfaces so the Android emulator can access it via 10.0.2.2
    uvicorn.run(app, host="0.0.0.0", port=8000)

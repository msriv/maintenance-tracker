---
name: mobile-store-assets
description: >
  Generate complete App Store and Google Play Store listing content, ASO-optimized copy, and
  supporting assets for mobile apps. Use this skill whenever the user asks to: write an app store
  description, create Play Store listing copy, write an App Store description or subtitle, generate
  a keyword field for iOS, write app store "what's new" release notes, create promotional text for
  the App Store, write a short description for Google Play, get ASO (App Store Optimization) help,
  write a privacy policy for a mobile app, prepare app store screenshots captions, or do anything
  related to submitting an app to the App Store or Play Store. Also triggers for: "help me write
  my store listing", "generate release notes", "write my app description", "what keywords should
  I use", "help me get discovered on the app store", "prepare my Play Store page". Do NOT use for
  code generation, CI/CD, or UI design.
---

# Mobile Store Assets Skill

## Step 0 — Gather Input

Ask the user for the following if not provided:

1. **App name**: What is the app called?
2. **One-paragraph description**: What does the app do? (Don't worry about word count — just describe it naturally.)
3. **Target audience**: Who is this for? (e.g., freelancers, parents, fitness enthusiasts, small business owners)
4. **Key features** (3–7): What are the most important things users can do?
5. **Target stores**: App Store, Play Store, or both?
6. **Competitor apps** (optional): Name 1–3 competitors — useful for keyword gap analysis.
7. **Data the app collects** (for privacy policy): e.g., name, email, location, camera, payments?

If the user can't answer all questions, proceed with what they give and note any assumptions.

---

## Step 1 — Generate Play Store Content

### App Title (≤50 characters)
- Lead with the app name, then add a high-value keyword phrase.
- Example: `TaskFlow — Team Task Manager`
- Avoid keyword stuffing. Google penalizes titles that look like keyword lists.
- Count characters carefully.

### Short Description (≤80 characters)
- One punchy sentence capturing the core value proposition.
- Must make someone want to tap "Read more."
- Include 1–2 high-intent keywords naturally.
- Example: `Manage tasks, track deadlines, and collaborate with your team.`

### Full Description (≤4000 characters)
Structure:
```
[Opening hook — 2-3 sentences addressing the user's pain point]

[Core value proposition — what makes this app different]

KEY FEATURES:
★ [Feature 1 — benefit-focused, not feature-focused]
★ [Feature 2]
★ [Feature 3]
★ [Feature 4]
★ [Feature 5]

[Social proof placeholder — "Join 10,000+ users who..." or leave for user to fill]

[CTA — "Download free today" or equivalent]

[Support / contact line — optional]
```

Rules:
- First 167 characters appear before the "Read more" fold — make them count.
- Use plain text formatting (★ bullets, ALL CAPS section headers). No Markdown.
- Weave keywords naturally throughout. Do not list keywords at the bottom.
- Write in second person ("you", "your") and present tense.

### Feature Graphic Text Suggestions
The feature graphic (1024×500px) is the banner at the top of the Play Store listing. Suggest 3 tagline options the designer can use:
1. Short punchy tagline (5–7 words)
2. Benefit statement (8–12 words)
3. Social proof variant ("Trusted by X users")

### What's New Template (for updates)
```
Version [X.X]:
• [Most important fix or feature]
• [Second change]
• [Third change]
Bug fixes and performance improvements.
```

### Content Rating Questionnaire Guidance
Brief walkthrough of key IARC questions:
- Violence: does the app show violence or allow users to create violent content?
- Sexuality: does the app contain sexual content or nudity?
- Language: does the app contain strong language?
- Substances: does the app reference drugs or alcohol?
- Social: does the app allow users to interact with each other?

Recommend the expected rating based on the app description.

---

## Step 2 — Generate App Store Content

### App Name (≤30 characters)
- Core brand name only. Keywords go in the Subtitle.
- Example: `TaskFlow`

### Subtitle (≤30 characters)
- Most important keyword phrase that won't fit in the name.
- Example: `Team Task & Project Manager`
- Apple indexes this field for search.

### Description (≤4000 characters)
Same structure as Play Store but slightly more polished — Apple's editorial style favors clarity and simplicity. Avoid excessive superlatives. Do not include pricing (violates App Store guidelines).

### Promotional Text (≤170 characters)
- Shown above the description, can be updated WITHOUT a new app review.
- Use for time-sensitive messages: launch discounts, seasonal features, awards.
- Example: `🎉 Introducing TaskFlow 2.0 — Redesigned from the ground up. Download now!`

### Keyword Field (≤100 characters, comma-separated, no spaces after commas)
Rules:
- Don't repeat words already in the app name or subtitle (Apple already indexes those).
- Don't include competitor names (guideline violation).
- Use singular form when it covers plural naturally.
- Separate with commas, NO spaces (each space wastes a character).
- Example: `task,manager,productivity,team,project,planner,to-do,organizer,reminder,work`

### Screenshot Caption Text
Suggest 5 captions for portrait screenshots (one per screen):
```
Screenshot 1: [Hero feature]
"[Benefit statement — 5–8 words]"
Subtext: "[Supporting detail — 1 short line]"
```

### What's New Template (App Store)
Same format as Play Store but Apple reviewers read this. Keep it factual and professional.

---

## Step 3 — ASO Tips (Inline)

Include these tips inline with the generated copy:

**Keyword research approach**:
- Use your competitor apps' titles and subtitles as keyword inspiration.
- Search your category in the App Store / Play Store; note the autocomplete suggestions.
- Prioritize keywords with high relevance + medium competition over high-volume + high competition.
- For the Play Store: keywords in the full description are indexed. Use each important keyword 3–5 times naturally (not stuffed).

**Localization note**:
- Even a basic translation to the top 3 markets (e.g., Spanish, French, German) can increase installs significantly. Flag this if the user hasn't considered it.

**Review prompts**:
- Prompt users for a review after a positive action (completing a task, finishing a session), not on first launch. Apple's `SKStoreReviewRequest` API allows 3 prompts per year.

---

## Step 4 — Privacy Policy Skeleton

Generate a `privacy-policy.md` based on what data the app collects. Include:

```markdown
# Privacy Policy — [App Name]

**Last updated:** [Date]

## 1. Information We Collect
[Based on user's answer: list each data type: account info, usage analytics, location, camera, etc.]

## 2. How We Use Your Information
[Match each data type to its use: personalization, analytics, support, payments]

## 3. Data Sharing
We do not sell your personal information. We may share data with:
- [Analytics provider, e.g., Firebase Analytics]
- [Payment processor, if applicable]
- [Cloud storage provider]

## 4. Data Retention
[How long data is kept; how users can request deletion]

## 5. Your Rights
[GDPR rights if EU users targeted; CCPA if California users targeted]

## 6. Security
[Brief description of security measures: encryption at rest/transit, etc.]

## 7. Children's Privacy
[COPPA statement if app is not directed at children under 13]

## 8. Contact
[Email address for privacy inquiries]
```

Note: This is a starting skeleton. Advise the user to have a qualified legal professional review it before publishing.

---

## Output Format

Deliver everything as a single Markdown document with these clearly separated sections:

```
# App Store Assets — [App Name]

## Google Play Store
### Title
### Short Description
### Full Description
### Feature Graphic Text Options
### What's New Template
### Content Rating Guidance

## Apple App Store
### App Name
### Subtitle
### Description
### Promotional Text
### Keyword Field
### Screenshot Captions
### What's New Template

## ASO Tips & Recommendations

## Privacy Policy
```

Then deliver `privacy-policy.md` as a separate file.

---

## Example

**Input:**
> "My app is called FitTrack. It helps people log workouts, track calories, and see progress charts. Target audience: fitness beginners. Key features: workout logging, calorie tracking, progress photos, weekly summaries, Apple Health sync."

**Expected output (abridged):**

**Play Store Title**: `FitTrack — Workout & Calorie Tracker` (39 chars)
**Short Desc**: `Log workouts, track calories, and see your fitness progress.` (60 chars)
**App Store Subtitle**: `Workout Log & Calorie Tracker` (29 chars)
**Keywords**: `fitness,workout,calorie,log,tracker,exercise,gym,health,diet,weight`
**Promo Text**: `🏋️ Start your fitness journey today — log workouts and track progress in minutes!`

---

## Checklist Before Responding

- [ ] All character limits verified (count manually if uncertain)
- [ ] Keywords in Play Store full description woven in naturally (3–5x each)
- [ ] App Store keyword field has no spaces after commas and no repeated words from name/subtitle
- [ ] Promotional text is updatable without app review — remind user
- [ ] Privacy policy reflects only the data the user says the app collects
- [ ] ASO tips included
- [ ] Legal disclaimer on privacy policy included
- [ ] Output structured as one master markdown + separate privacy-policy.md

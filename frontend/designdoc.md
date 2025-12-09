

# **FluxShare Frontend Design Document (Simplified, Single Page UI)**

### Using React JS and optional UI library (Material UI 
---

## **1. High Level UI Concept**

The entire user flow occurs inside a **single landing page**.
There are **no route changes**, no navigation, no additional pages.

At the center of the screen sits a **large rectangular card**.
Inside this card:

1. A **tab selector** at the top
2. A **dynamic form** that switches between File, Text, and Code sharing
3. A **submit button**
4. After submission, a **success view** replaces the form showing:

   * Share link
   * QR code
   * Expiry time
   * Copy button

This keeps the app extremely clean and easy to use.

---

## **2. Layout Description**

### **Main Layout**

The app body will have:

* Center aligned container
* A minimal header with the app name (optional)
* A main card with slight shadow

### **Card Structure**

```
+-------------------------------------------+
|   File | Text | Code   <-- Tabs           |
+-------------------------------------------+
|                                         |
|     Dynamic Content Area (Form or       |
|     Success view based on state)        |
|                                         |
+-------------------------------------------+
```

Tabs behave like browser tabs.
When the user clicks one, the form below switches smoothly.

---

## **3. Tab Behaviors**

### **File Tab (default selected)**

Shows:

* Drag and drop upload region
* File list preview
* Options:

  * expiry time dropdown
  * password field
  * view once toggle
  * notes
  * max downloads or max views

### **Text Tab**

Shows:

* Large textarea for text
* Same options section
* Word count (optional)

### **Code Tab**

Shows:

* Code editor
* Language dropdown
* Options: password, expiry, view once, notes

---

## **4. Success View (Shown After Creating a Share)**

After clicking Submit and backend responds, the dynamic content area switches to:

```
Share created successfully!

Share URL: https://fluxshare.com/s/abcd123
[Copy] button

QR Code displayed here

Expires in: X hours
View Once: Yes/No

Button: Create Another Share
```

When user clicks “Create Another Share”, it resets the form and switches back to the previous tab.

---

## **5. Form Structure**

Each tab shows a different primary input area, but the “Common Options” section remains consistent.

### **Common Options Section**

* Expiry time dropdown
* Password input (optional)
* View once toggle
* Notes field
* Max downloads
* Max views

You can keep these in a collapsible panel for cleanliness.

---

## **6. Validation & Error UX**

Simple and clear messages:

* “File too large”
* “Password required for this link”
* “Share expired”
* “Rate limit exceeded, please try again later”

On error, the central card shakes lightly or highlights red.

---

## **7. Content Preview Logic (Inside File Tab)**

Before creating the share:

* Show a file list with filename, size and an icon
* If user removes a file, update list immediately

There is no preview after generating the share because the share is already uploaded.

---

## **8. Styling Guidelines**

* Soft shadows
* Rounded corners
* Light color palette, optional dark mode
* Smooth transition when switching tabs

Use a UI library if you want faster development.

---

## **9. State Management**

All state can live inside a single React component or split into small components:

* Active tab
* File list
* Text input
* Code input
* Options (expiry, password, view once, notes, limits)
* Share result (URL, QR code)

No need for Redux, Context, or heavy state management.

---

## **10. API Interaction Flow**

The design doc does not describe exact API usage, the coding agent will follow the API doc.
But the frontend behavior is:

### When clicking Submit:

1. Validate inputs
2. Send request to corresponding backend API
3. Show loading spinner
4. When success:

   * Switch dynamic content to success view
   * Show link and QR
5. When error:

   * Show clean error message 

---

## **11. Mobile Responsiveness**

* Tabs wrap into a horizontal scroll if needed
* Main card shrinks to full width on mobile
* Code editor collapses into a text input on very small screens

---

## **12. Optional Enhancements**

These are not required but nice to have:

* Animation when switching tabs
* Progress bar during file upload
* Copy button turns into “Copied!” animation
* Countdown timer until expiration

---
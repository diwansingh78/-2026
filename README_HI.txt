मतदाता प्रबंधन 2026 — Android Studio प्रोजेक्ट

इस प्रोजेक्ट में आपके दिए HTML सॉफ्टवेयर को Android WebView ऐप में रखा गया है और Bluetooth Classic SPP (UUID 00001101-0000-1000-8000-00805F9B34FB) प्रिंटर कनेक्शन का native bridge जोड़ा गया है।

APK बनाने के लिए:
1. Android Studio में यह पूरा फोल्डर खोलें।
2. Gradle sync पूरा होने दें (Android Gradle Plugin 8.7.3, SDK 35 आवश्यक)।
3. Build > Build Bundle(s) / APK(s) > Build APK(s) चुनें।
4. APK app/build/outputs/apk/debug/app-debug.apk में बनेगी।

प्रिंटर:
- फोन की Bluetooth Settings में थर्मल प्रिंटर को पहले Pair करें।
- ऐप में प्रिंटर कनेक्ट बटन दबाकर paired device चुनें।
- यह SPP Classic Bluetooth के लिए है; BLE-only प्रिंटर के लिए अलग GATT सपोर्ट चाहिए।

महत्वपूर्ण सीमाएँ:
- यह तैयार Android Studio source project है, compiled APK नहीं। इस environment में Android SDK/Gradle उपलब्ध नहीं होने से APK compile नहीं की जा सकी।
- ESC/POS प्रिंटरों में हिन्दी अक्षरों का आउटपुट firmware/code-page पर निर्भर करता है। कई 58mm प्रिंटर UTF-8 हिन्दी सीधे नहीं छापते; वास्तविक प्रिंटर पर टेस्ट आवश्यक है।
- मूल HTML की बाकी सुविधाएँ उसी फाइल से आती हैं; यह पैकेज backend/cloud sync या डेटा माइग्रेशन नहीं जोड़ता।

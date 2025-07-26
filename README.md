# PayWise 💸

**PayWise** is a simple and modern daily finance tracking app built with **Kotlin** and **Firebase**. Users can add, manage, and monitor their income and expenses — all presented with visual insights like pie charts and clean UI components.

---

## ✨ Features

- 📌 Add income and expense transactions
- 📝 Edit or delete transactions
- 📅 Select and display custom transaction dates
- 📊 Visualize data with modern pie charts
- 🔐 User authentication with Firebase Auth
- ☁️ Cloud storage using Firebase Firestore
- 💱 Format all numbers into Indonesian Rupiah
- 🎨 Material Design + View Binding

---

## 🚀 Tech Stack

- **Kotlin**
- **Firebase Auth** (Authentication)
- **Cloud Firestore** (Real-time NoSQL DB)
- **MPAndroidChart** (Pie chart visualization)
- **Material Components**
- **View Binding**
- **Google Material Date Picker**
- **RX Binding**

---

## 📦 Installation

1. Clone this repository

```bash
git clone https://github.com/Wrdn28/PayWise.git
```

2. Open the project in Android Studio

3. Add your Firebase configuration:

    - Go to [Firebase Console](https://console.firebase.google.com)
    - Create a new project or use an existing one
    - Enable **Authentication > Email/Password**
    - Enable **Cloud Firestore** and choose "Start in test mode"
    - Download `google-services.json` and place it into:
      ```
      app/google-services.json
      ```

4. Sync Gradle and build the project

5. Run the app on your emulator or Android device (API level 21+)

---

## 💡 Helper Utility

**CurrencyHelper.kt**  
To format numbers into Indonesian Rupiah (Rp):

```kotlin
fun formatRupiah(amount: Int): String {
    val localeID = Locale("in", "ID")
    val format = NumberFormat.getCurrencyInstance(localeID)
    return format.format(amount).replace(",00", "")
}
```

---

## ✅ To-Do

- [x] Add date input for transactions
- [x] Implement pie chart statistics
- [x] Format currency to Indonesian Rupiah
- [ ] Add dark mode support
- [ ] Export transaction data to PDF or Excel
- [ ] Date range filter (weekly/monthly)

---

## 🙌 Credits

- [Firebase](https://firebase.google.com/)
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
- [Material Design Components](https://material.io/)
- [Dicoding Indonesia](https://www.dicoding.com/)
- Icons from [Material Icons](https://fonts.google.com/icons)
- RX Binding from [JakeWharton](https://fonts.google.com/icons)

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

> “Spend wisely, track easily — with PayWise.” 💸
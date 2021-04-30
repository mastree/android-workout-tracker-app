# Workout App

## Deskripsi Aplikasi

Sebuah aplikasi android untuk menunjang aktivitas workout sesorang dengan spesifikasi sebagai berikut ini:
1. Sport News : pengguna dapat melihat berita olahraga yang diperoleh dari Indonesia Sport New API.
2. Training Tracker: pengguna dapat melakukan mulai dan selesai untuk **pelacakan** olahraga dengan jenis berikut inti
    1. Sepeda : melacak jarak dan rute yang dilalui menggunakan GPS
    2. Walking/Running : melacak jumlah step yang dilakukan pengguna
3. Training History: terdapat training history yang menyimpan seluruh kegiatan latihan dengan ditampilkan dalam bentuk calendar
4. Training Scheduler: pengguna dapat menentukan jadwal latihan yang dapat dikustomisasi untuk berjalan secara:
    a. Satu waktu spesifik
    b. Rutin per hari
    c. Rutin per hari tertentu

## Cara Kerja Aplikasi
### Sport News
1. Melakukan request data berita menggunakan Indonesia Sports News API
2. Data yang direquest di-bind menggunakan MVVM sehingga NewsAdapter bisa di-update
3. Pada setiap data diberikan onClickListener untuk membuka website pada fragment baru
4. Pada menu utamanya juga diberikan kondisi apabila layarnya landscape (ketika landscape spanCount-nya 2)
### Training Tracker
1. Menampilkan halaman start tracking dengan pilihan aktivitas dan Start
2. Jika diklik start maka tracking akan dimulai dan akan melakukan permintaan lokasi untuk melakukan tracking
3. Setelah tracking selesai maka hasil tracking tersebut akan disimpan ke dalam database
4. Kemudian hasilnya akan ditampilkan pada halaman TrackerViewer
### Training History
1. Menampilkan fragment Training History untuk menampilkan calendar dan pick date yang akan dicek
2. Date akan dipassing ke fragment berikutnya dan digunakan sebagai kondisi history yang akan ditampilkan
3. Apabila salah satu history di-klik, maka aplikasi akan membuka tracker viewer untuk history yang bersangkutan
### Traning Scheduler
1. Menampilkan fragment Training Scheduler
2. Data schedule di-bind menggunakan MVVM
3. Jika menekan tombol floating action button, aplikasi akan membuka fragment add schedule
5. Data yang ditambahkan akan langsung di-update ke database dan dibuatkan alarm

## Library dan Justifikasinya
1. Picasso: untuk melakukan download gambar dari link eksternal dan menyimpan image tersebut
2. Glide: untuk memasukkan bitmap pada suatu imageView
3. Retrofit: untuk melakukan request terhadap eksternal API
4. Converter-GSON: Untuk mengolah JSON menjadi objek Java atau sebaliknya
5. OkHttp: Untuk melakukan interceptor terhadap request
6. Maps SDK Android: Untuk melakukan tracking location pada peta
7. Coroutines: Untuk melakukan coroutines dan lifecycle
8. Room: Untuk mengimplementasi database

## Screenshoot Aplikasi
### Sports News
![News](assets/news.png)  
![News Web](assets/newsweb.png)  
![News Landscape](assets/newslandscape.png)  

### Tracker
![Tracker First](assets/trackerfirst.png)  
![Tracker Running](assets/trackerrunning.png)  
![Tracker Viewer Running](assets/trackerviewerrunning.png)  
![Tracker Cycling](assets/trackercycling.png)  
![Tracker Viewer Cycling](assets/trackerviewercycling.png)  
![Tracker Foreground](assets/trackerforeground.png)  

### History
![History Calendar](assets/historycalendar.png)  
![History Recycler](assets/historyrecycler.png)  
![History Viewer](assets/historyviewer.png)  

### Scheduler
![Scheduler Awal](assets/schedulerawal.png)  
![Scheduler Biasa](assets/schedulerbiasa.png)  
![Scheduler JustOnce dan Sepeda](assets/schedulersepedajustonce.png)  
![Scheduler NotJustOne dan Hari2](assets/schedulernotjustonce.png)  
![Scheduler Akhir](assets/schedulerakhir.png)  
![Scheduler Delete](assets/schedulerdelete.png)  

## Pembagian Kerja
### Fatkhan Masruri 13518053
1. Layout History
2. Dikumentasi Readme
### Iqbal Naufal 13518074
1. Fitur Scheduler (notification)
### Muhammad Kamal Shafi 13518113
1. Fitur SportNews
2. Fitur History
3. Scheduler Layout, UI dan Logic
4. Navigation
### Faris Rizki Ekananda 13518125
1. Fitur Tracking
2. Init Database, TrackerDAO dan Main Application
3  Integrasi Maps SDK Android Google
4. Tracker Viewer Fragment
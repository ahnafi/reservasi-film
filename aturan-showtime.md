semua studio mulai jam 9.00
- tambah showtime 
> jika showtime dari studio A (pertama kali ditambah)
> maka mulai jam 9.00
> jika showtime kedua dan seterusnya (maka showtime pertama + jam tayang film tersebut)
> dan seterusnya

> jika showtime sudah sampai jam 9.00 malam atau mendekati jam 9.00 malam , 
> maka tidak bisa menambah showtime lagi
> bisa jika showtime itu pas sampai jam 9 malam

misal mau di tambah jam istirahat ketika pergantian studio 
> katakan 15 menit 
> maka hitungan untuk showtime studio kedua dan seterusnya dengan rumus
> (showtime sebelumnya + 15 menit istirahat + waktu film tayang )



algoritma , reset jam waktu

- ambil semua data showtime 
- urutkan dari yang id terkecil 
- jika tidak ada maka membuat showtime di awal waktu atau jam 9 
- jika yang pertama di hapus maka update waktu semua showtime dengan showtime pertama menjadi jam 9 
- dan seterusnya (showtime sebelumnya + 15 menit istirahat + waktu film tayang )
#!/bin/bash
ulazi=ulaz*
mapfile -t otisci < otisci.dec


for ulaz in $ulazi;
    do 
        hash1=`openssl passwd -1 -salt ulaz14 -in "$ulaz"`
        hash2=`openssl passwd -5 -salt ulaz23.txt -in "$ulaz" `
        hash3=`openssl passwd -1 -salt VNYawEnc  -in "$ulaz" `
        hash4=`openssl passwd -apr1 -salt ++++++++ -in "$ulaz" `
        hash5=`openssl passwd -6 -salt SALT_ulaz26.txt -in "$ulaz" `
        hash6=`openssl passwd -6 -salt txt.12zalu -in "$ulaz" `
        for otisak in "${otisci[@]}";do
           
           

            if [[ "$hash1" == "$otisak" ]];
                then echo "Datoteka: $ulaz odgovara $hash1"         
            elif [[ "$hash2" == "$otisak" ]];
            then echo "Datoteka: $ulaz odgovara $hash2"
            elif [[ "$hash3" == "$otisak" ]];
                then echo "Datoteka: $ulaz odgovara $hash3"
            elif [[ "$hash4" == "$otisak" ]];
                then echo "Datoteka: $ulaz odgovara $hash4"
            elif [[ "$hash5" == "$otisak" ]];
                then echo "Datoteka: $ulaz odgovara $hash5"
            elif [[ "$hash6" == "$otisak" ]];
                then echo "Datoteka: $ulaz odgovara $hash6"
            fi
done
done
        
    

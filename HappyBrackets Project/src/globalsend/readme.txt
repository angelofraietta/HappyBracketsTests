
The Purpose of the tests is to see why packets of daa are being lost
A global trigger is sent from a master device and monitored on slave devices
There is both wired and wireless slaves

Test is done with master as wired and wireless

If both the wired and wireless slaves are missing trigger counts then
the most likely cause is message lost at sent stage

If wired send does not lose data but wireless send does, then issue maybe a wireless send

We may need to look at whether broadcast messages are lost


Adding smart controller network

sudo su
wpa_passphrase "Smart Con" "lJSw75vL" >> /etc/wpa_supplicant/wpa_supplicant.conf

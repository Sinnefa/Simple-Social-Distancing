# Simple-Social-Distancing

Estimate the number of interactions around you so you can try to keep it as small as you can, as if it was a game.

A concept android application to count nearby discoverable bluetooth devices to facilitate social distancing.
A graph theory based computer simulation I made to show the benefits of social distancing on disease propagation: https://youtu.be/Nwrh_Vm4TpE
If anyone knows or wants to further implement this idea here is how it works:

1. Give the user an estimate of any "bluetooth activity" around at the moment to help him/her reduce it
2. Give the user an estimate of how many "human activities" around you you met on average in the past days
3. In the future days, help the user to keep it numbers lower than previous days
4. It is a matter of probability, the less people you meet the less probable it is that you get infected

Free to clone this project or contact me.


# Description
Unlike other apps, this app scans for any possible contact just to help you reduce the chance of encountering someone.
This application continuously scans for nearby discoverable bluetooth devices. It shows how many devices are around you in time.
I developed this app to show that it is possible to estimate how many interactions you have during the day. By reducing this number, looking at the interface, you can reduce your interactions through the days and possible reduce the chance of being infected.

![screenshot](https://github.com/Sinnefa/Simple-Social-Distancing/blob/master/markdown_imgs/banner_full.png)


# WARNING
This is just a concept application, it is not meant to be an official app against virus infections.
* The app doesn't use internet, data will never leave you device. Position included (Coarse position is not localization anyway)
* Constant use of the apps drains battery
* One bluetooth devices does not mean one person
* Bluetooth distance is not the "safe distance" suggested by the law of your country
* Bluetooth is very limited and a very rough approximation of this idea to work properly.

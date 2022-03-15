$package = 'ru.etu.battleships'
$seed = 120
$event_count = 10000
adb shell monkey -p $package -s $seed -v $event_count

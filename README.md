# TouchControlServer🖱

TouchControlServer is a PC Java Program designed to receive touch data, controlling the mouse on pc.

## Installation

Download the latest release [here](https://github.com/Draylon/TouchControlServer/releases)

## Usage

° This requires Java installed;

° This requires ADB installed.

This app is the second to [TouchControl](https://github.com/Draylon/TouchControl).

After connecting the phone on usb and opening [TouchControl](https://github.com/Draylon/TouchControl), run in command line:
```bash
adb forward tcp:5545 tcp:3322
adb forward tcp:5546 tcp:3323
java -jar <file>.jar
```

## Contributing
Pull requests are welcome.

For major changes, please open an issue first to discuss what you would like to change.

## License
[MIT](https://choosealicense.com/licenses/mit/)
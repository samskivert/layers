all:
	find . | grep .java$ | grep -v '#' | grep -v './test' | xargs javac -cp .:./lib/colorchooser.jar

clean:
	find . |grep \.class$ | xargs rm

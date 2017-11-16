// Same as MSS_Slot but without a pv buffer

MSS_Slot2 {
	var <>s, <>playGroup, <recordGroup, <>state, <>playSynth, <>recordSynth,<>mapValue, <>mapKeys, <>mappedSynthKeys,<>buffer, <>defaultInBus, isMute;
	var <>baseArgs, bufMaxSize, recordInst, <>waitForBufSize, resetDict;


	*new {
		arg s=Server.default, stateDict, baseArgs, mapValueFunction, mapKeysDict, resetDict, playGroup=nil, recordGroup=nil, recordSynth=\bufRecord, bufMaxSize=16.0, defaultInBus=8;
		^super.new.init(s, stateDict, baseArgs, mapValueFunction, mapKeysDict, resetDict, playGroup, recordGroup, recordSynth, bufMaxSize, defaultInBus);
	}

	init {
		arg sInit, stateInit, baseArgsInit, mapValueInit, mapKeysInit, resetDictInit, playGroupInit, recordGroupInit,recordSynthInit, bufMaxSizeInit, defaultInBusInit;
		s=sInit ? Server.default;
		bufMaxSize=bufMaxSizeInit;
		state=stateInit.copy ? Dictionary[\bufSize->0,\joyButton->0,\playButton->0,\mode->2];
		defaultInBus=defaultInBusInit;
		baseArgs=baseArgsInit.copy;
		recordInst=recordSynthInit;
		mapValue = mapValueInit ? {|state, key|
			switch(key)
			{\play} {state[\playButton]}
			{\instrument} {\bufPlayTape}
			{\bufSize} {state[\bufSize]}
		};
		mapKeys =  mapKeysInit.copy ? Dictionary[\playButton->\play,\bufSize->\bufSize];
		mappedSynthKeys =  mapKeys.filteredValues;
		resetDict=resetDictInit.copy ? Dictionary[];
		isMute=false;
		this.reload(playGroupInit, recordGroupInit);
		this.setBuffers;


	}

	reload {|playGroup, recordGroup|
		playSynth=nil;
		recordSynth=nil;
		this.set(\playButton, 0);
		this.set(\joyButton, 0);
		this.setGroups(playGroup, recordGroup);
		this.setOSC;
	}

	setGroups {|playGroupInit, recordGroupInit|
		recordGroup=recordGroupInit?s;
		playGroup=playGroupInit?s;
	}


	setOSC {
		if (waitForBufSize!=nil) {waitForBufSize.free};
		waitForBufSize = OSCFunc({|msg|
			if (msg[2].asInteger==buffer.bufnum)	{
				msg.postln;
				if (msg[3]>0) {
					this.set(\bufSize, msg[3]);
					buffer.fill(state[\bufSize],buffer.numFrames-state[\bufSize],0);
					if (playSynth==nil) {
						this.reset;
					};
					if (recordSynth!=nil) {
						recordSynth.free;
						recordSynth=nil;
					};
				}

			}

		},'/tr');
	}

	setBuffers {
		Routine({
			if (playSynth!=nil) {playSynth.free; playSynth=nil;};
			"setting up buffers".postln;
			this.freeBuffers;
			buffer = Buffer.alloc(s,bufMaxSize*s.sampleRate, 1);
			s.sync;
			buffer.bufnum.postln;
		}).play;
	}

	play {
		var mappedArray,jointArray,synth=nil;

		mappedArray=mappedSynthKeys.collect({|key|
			[key, mapValue.value(state, key)]}).flatten;
		jointArray=baseArgs++[\bufnum, buffer.bufnum]++mappedArray;
		jointArray.postln;
		("mode"++":"++state[\mode]).postln;
		if (isMute==false) {
			synth=Synth.tail(playGroup, mapValue.value(state,\instrument), jointArray);
		};
		^synth;
	}

	mute {|val|
		if (val) {
			isMute=true;
			this.freeSynth;
		} {
			if ((state[\joyButton]+state[\playButton]>0) && (playSynth==nil)) {
				this.play;
			}
		}
	}

	set {|key, val|
		var synthKeys;
		if (key==\mode) {this.setMode(val)}
		{
			state[key]=val;
			if (playSynth!=nil) {
				synthKeys= mapKeys[key]?key;
				//synthKeys.postln;
				synthKeys.do({|synthKey|
					//synthKey.postln;
					playSynth.set(synthKey, mapValue.value(state,synthKey));
				});
			}
		}
	}

	setMode {|mode|
		if (state[\mode]!=mode) {
			state[\mode]=mode;
			if (playSynth!=nil) {
				playSynth.set(\gate,0);
				playSynth=this.play;
			}
		}
	}


	playButton {|val=true|
		if (val) {
			this.set(\playButton,1);
			if (playSynth==nil)	{
				playSynth=this.play;
			}
		} {
			this.set(\playButton,0);
			if (state[\joyButton]==0)
			{
				this.freeSynth;

			} {
				this.set(\playButton,0);
			};


			("playButton:"+state[\playButton]).postln;
		};
	}

	joyButton {|val=true,fire=0|
		if (val) {
			this.set(\joyButton,1);
			this.set(\fire,fire);

			if (playSynth==nil)	{
				playSynth=this.play;
			}
		} {
			this.set(\joyButton,0);
			if (state[\playButton]==0)
			{
				this.freeSynth;

			} {
				this.set(\joyButton,0);
				this.set(\fire,0);
			};


			("joyButton:"+state[\joyButton]).postln;
		};
	}

	record  {|val=true, threshold=0.0, args, inBus|
		if (val) {
			var localInBus=inBus?defaultInBus;
			recordSynth=Synth.tail(recordGroup,recordInst,
				[\bufnum, buffer.bufnum,
					\inBus, localInBus,
					\threshold, threshold,
					\bufMaxSize, bufMaxSize
			]++if (args!=nil) {args.getPairs});
			if (args!=nil) {
				if (args[\dur]!=nil) {this.set(\bufSize, args[\dur]*s.sampleRate)};
			};
			"record".postln;
		} {
			this.freeRecord;
			"record stop".postln;
		}
	}

	reset {
		resetDict.pairsDo({|key,val| this.set(key, val)});
	}

	write { |fileName|
		buffer.write(fileName, "aiff", "int32");
		("buffer saved to file:"++fileName).postln;
	}

	read { |fileName |
		var isOn=false;
		if (playSynth!=nil) {playSynth.free; playSynth=nil;};
		this.freeBuffers;
		buffer=Buffer.readChannel(s,fileName, channels: [0], action:
		{
				this.set(\bufSize,buffer.numFrames);
				("buffer loaded, file:"++fileName++" - size:"++state[\bufSize]).postln;
		});
	}

	isJoy {
		var return;
		^(state[\joyButton]>0);
	}

	isPlay {
		var return;
		^(state[\playButton]>0);
	}

	freeBuffers {
		buffer.free;
	}

	freeSynth {
		if (playSynth!=nil) {
			playSynth.set(\gate, 0);
			playSynth=nil;
		}
	}

	freeRecord {
		if (recordSynth!=nil) {
			recordSynth.set(\gate,0);
			recordSynth=nil;
		}
	}

	free {
		this.freeSynth;
		this.freeRecord;
		this.freeBuffers;
		waitForBufSize.free;
	}

}
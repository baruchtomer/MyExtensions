RecordOutMulti {

	var   <server,  <recordPath, <tempfilename, <inMono, <inSt, <addTime,
	<recordOutSynthSt,<recordOutSynthMono,<recordOutBufSt, <recordOutBufMono, <time=nil;

	*new { arg server=Server.local, recordPath=nil, filename="SC", inMono=nil, inStereo=nil,addTime=true;
		^super
		.newCopyArgs(server,recordPath, filename, inMono, inStereo, addTime)
		.init();
	}

	init {
		var inStList=List.new(0), inMonoList=List.new(0);
		SynthDef("recordOutSt", {arg bufnum,inBus;
			DiskOut.ar(bufnum, In.ar(inBus,2));
		}).add;

		SynthDef("recordOutMono", {arg bufnum,inBus;
			DiskOut.ar(bufnum, In.ar(inBus,1));
		}).add;
		if (recordPath.isNil) {recordPath=thisProcess.platform.recordingsDir++"/"};
		if ((inMono.size==0) && (inMono!=nil)) {inMono=[inMono]};
		if ((inSt.size==0) && (inSt!=nil)) {inSt=[inSt]};
		recordOutBufSt=Array.fill(inSt.size,{Buffer.alloc(server, 65536, 2)});
		recordOutBufMono=Array.fill(inMono.size,{Buffer.alloc(server, 65536, 1)});
	}

	prepare { arg filename;
		"preparing for record".postln;
		if (addTime) {
			time=Date.localtime.stamp;
		};
		filename=filename?tempfilename;
		inSt.size.do({|i|
			recordOutBufSt[i].write(recordPath++filename++"_"++time++".stereo."++(i+1)++".aiff", "aiff", "int24", 0, 0, true)});
		inMono.size.do({|i|
			recordOutBufMono[i].write(recordPath++filename++"_"++time++".mono."++(i+1)++".aiff", "aiff", "int24", 0, 0, true)});
	}

	start {
		"recording".postln;
		recordOutSynthSt=Array.fill(inSt.size,{|i|
			Synth.tail(server, \recordOutSt, [\bufnum, recordOutBufSt[i],\inBus,inSt[i]])
		});
		recordOutSynthMono=Array.fill(inMono.size,{|i|
			Synth.tail(server, \recordOutMono, [\bufnum, recordOutBufMono[i],\inBus,inMono[i]])
		});
		CmdPeriod.doOnce {
			recordOutSynthSt = nil;
			recordOutSynthMono = nil;
			recordOutBufSt.do(_.close);
			recordOutBufMono.do(_.close);
		}
	}

	stop {
		recordOutSynthSt.do(_.free);
		recordOutSynthMono.do(_.free);
		recordOutBufSt.do(_.close);
		recordOutBufMono.do(_.close);
		recordOutSynthSt=nil;
		recordOutSynthMono=nil;
		"record finished".postln;
	}

	record { arg filename=nil;
		Routine({
			this.prepare(filename);
			server.sync;
			this.start;
		}).play
	}

	free {
		recordOutBufSt.do(_.free);
		recordOutBufMono.do(_.free);
	}
}








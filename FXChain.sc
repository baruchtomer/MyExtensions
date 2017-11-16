FXChain {
	var <>groupChain, <>playSynth, <>state, <>synthChain, <>numSlots, <>isMute;

	*new {
		arg tail=Server.local, numSlots=2, synthChain, stateDict;
		^super.new.init(tail, numSlots, synthChain, stateDict);
	}

	init {
		arg tail, numSlotsInit, synthChainInit, stateDictInit;
		var defaultState=Dictionary[\mix->1.0, \param1->0.0,\param2->0.0,\gate->1];
		numSlots=numSlotsInit;

		state=Array.fill(numSlots, {stateDictInit.copy ? defaultState.copy});
		groupChain=Array.fill(numSlots, {Group.tail(tail)});
		synthChain=synthChainInit;
		playSynth=nil!numSlots;
		isMute=false;
	}

	play {|slot, val=true|
		if (val) {
			state[slot][\gate]=1;

			if ((playSynth[slot]==nil) && (isMute==false) && (state[slot][\mix]>0))  {
				playSynth[slot]=Synth.tail(groupChain[slot], synthChain[slot],state[slot].asKeyValuePairs);
			}
		} {
			state[slot][\gate]=0;
			this.freeSynth(slot);
		}
	}

	mute {|val|
		if (val) {
			isMute=true;
			numSlots.do({|i| this.freeSynth(i)});
		} {
			isMute=false;
			numSlots.do({|i|
				if (state[i][\gate]>0) {this.play(i,true)}
			});
		}
	}


	freeSynth {|slot|
		if (playSynth[slot]!=nil) {
			playSynth[slot].set(\gate, 0);
			playSynth[slot]=nil;
		}
	}

	free {
		groupChain.do({|group| group.free});
		playSynth.do({|synth| synth.free});
	}

	set {|slot, key, val|
		state[slot][key]=val;
		if (playSynth[slot]!=nil) {playSynth[slot].set(key, val)};
		if (key==\mix) {
			if (val==0) {
				this.freeSynth(slot);
			} {
				if (state[slot][\gate]>0) {
					this.play(slot, true);
				}
			}
		}
	}
}



		
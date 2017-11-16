+ Dictionary {
	getPairsValues { arg args;
		var result;
		args =  this.keys;
		args.do { |key|
			var val = this.at(key);
			val !? { result = result.add(key).add(val.value(args)) }
		};
		^result
	}
	defaults { arg default;
		default.pairsDo({|key, value|
			if (this[key].isNil) {
				this[key]=value
			}
		});
		^this
	}

	defaultsCopy { arg default;
		var new=this.deepCopy;
		default.pairsDo({|key, value|
			if (new[key].isNil) {
				new[key]=value
			}
		});
		^new
	}

/*	++ { arg aDict;
		var newDict;
		newDict = this.copy;
		^newDict.putAll(aDict);
	}*/
	makeArgArray { arg args;
		var list=List.newClear;
		args.do({|key|
			if (this.at(key)!=nil) {
				list.add(key);
				list.add(this.at(key));
			}
		});
		^list.asArray;
	}

	makeArgArrayValue { arg args;
		var list=List.newClear;
		args.do({|key|
			if (this.at(key)!=nil) {
				list.add(key);
				list.add(this.at(key).value);
			}
		});
		^list.asArray;
	}
/*
	updateValues { arg args;
		args.pairsDo({|key, value|
			this.put(key,value);

		});
		^this;
	}
*/
}

			
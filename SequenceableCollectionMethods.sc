+ SequenceableCollection {
	contains { arg item;
		this.do ({ arg i;
			if ( item == i, { ^true })
		});
		^false
	}
	removeLast {
		if (this.size>0) {^this.removeAt(this.size-1);}

	}
	removeFirst {
		if (this.size>0) {^this.removeAt(0)};
	}

}

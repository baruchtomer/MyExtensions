+ Dictionary {
	filteredValues {
		var last=nil,list=List[];
		this.values.flatten.sort.do({|item|
			if (item!=last) {list.add(item); last=item} {last=item}
		});
		^list.asArray;
	}
}
			
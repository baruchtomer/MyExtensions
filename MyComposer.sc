MyControl {
	var <pitches,root,<durations,baseDur,motifLength,motif;

	*new { arg root,baseDur,length;
		^super.new.init(root,baseDur,length);
	}
	init { arg newRoot,newBaseDur,newLength;
		root = newRoot ? 60;
		pitches = root+[0,1,3,4,6,7,9,10];
		baseDur=newBaseDur ? 0.2;
		durations = baseDur*[0.333,0.5,0.666,1,2];
		motifLength=newLength ? 6;
		motif=[0.0].dup(motifLength);
		motifLength.do({arg i;
			motif[i]=[pitches[pitches.size.rand],durations[durations.size.rand]];
		});
	}
	getMotif {
		^motif;

	}
	getMotifPitches {
		^motif.flop[0];

	}

	getMotifDurations {
		^motif.flop[1];

	}

	newMotif { arg newLength;
		motifLength=newLength ? 6;
		motif=[0.0].dup(motifLength);
		motifLength.do({arg i;
			motif[i]=[pitches[pitches.size.rand],durations[durations.size.rand]];
		});
	}
}

MyVariation {
	var motif;

	*new { arg motif;
		^super.new.init(motif);
	}

	init { arg newMotif;
		motif=newMotif ? [0,0];
	}
	getVariation {
		var variation=[0,0].dup(motif.size), i = 4.rand;
		switch (i)
		{0} {variation=motif}
		{1} {variation=motif.reverse}
		{2} {motif.size.do({|i| variation[i]=[motif[0][0]-(motif[i][0]-motif[0][0]),motif[i][1]];})}
		{3} {motif.size.do({|i| variation[i]=[motif[0][0]-(motif[i][0]-motif[0][0]),motif[i][1]];});
			variation=variation.reverse};
		^variation;
	}
}


MyComposer {
	var composition,motif,control,numVariations,variation,root,maxDur,baseDur,motifLength;

	*new { arg root, baseDur, maxDur, motifLength ;
		^super.new.init(root,baseDur,maxDur,motifLength);
	}

	init { arg newRoot,newBaseDur,newMaxDur,newMotifLength;
		root = newRoot ? 60;
		baseDur=newBaseDur ? 0.2;
		motifLength=newMotifLength ? 6;
		maxDur=newMaxDur ? 20;
		motif=MyControl(root,baseDur,motifLength).getMotif;
		variation=MyVariation(motif);
		composition=List();
		numVariations=(maxDur/motif.flop[1].sum).asInteger;
		numVariations.do({
			var thisVariation=variation.getVariation;
			thisVariation.do({|i| composition.add(i)});
		});
	}

	getComposition {
		^composition;
	}

	getCompositionPitches {
		^composition.flop[0];
	}
	getCompositionDurations {
		^composition.flop[1];
	}
}
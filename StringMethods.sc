+ String {
	loadMiditxt {
		var lastTime=0;
		var score=List[Dictionary[\note->0,\vel->0,\dur->0,\delta->0]];  // for dictionary

		var x=FileReader.read(this).postcs;

		var y=Array.fill(x.size,{|i|
			[
				x[i][0].asInteger / 1920.0,
				x[i][1],
				x[i][2].split($=)[1].asInteger,
				x[i][3].split($=)[1].asInteger,
				x[i][4].split($=)[1].asInteger]
		}
		);



		y.do({|ev, i|
			if (ev[1]=="On")
			{
				score.last[\delta]=ev[0]-lastTime;    //Dictionary
				score.add(Dictionary[\note->ev[3],\vel->ev[4],\dur->0,\delta->0]);  // Dictionary

				lastTime=ev[0];

			}
			{
				score.last[\dur]=ev[0]-lastTime; //Dictionary
			}

		});
		score.removeFirst;
		score.last[\delta]=1;
		^score.asArray;

	}

}

+ Server {
	useJack {
		"using Jack".postln;
		this.options.inDevice_("JackRouter");
		this.options.outDevice_("JackRouter");
	}
	useMotu {
		"using MOTU".postln;
		this.options.inDevice_("MOTU UltraLite mk3 Hybrid");
		this.options.outDevice_("MOTU UltraLite mk3 Hybrid");
	}
}
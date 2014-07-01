require 'mimic'

module MockServer
	MIMIC = Mimic.mimic(port: 2000, log: $stdout)

	def mimic
		MIMIC
	end
end

World(MockServer)

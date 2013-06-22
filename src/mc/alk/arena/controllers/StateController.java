package mc.alk.arena.controllers;

import mc.alk.arena.objects.MatchParams;
import mc.alk.arena.objects.MatchState;
import mc.alk.arena.objects.options.TransitionOption;
import mc.alk.arena.objects.pairs.JoinResult;
import mc.alk.arena.objects.queues.ArenaMatchQueue;
import mc.alk.arena.objects.queues.TeamJoinObject;
import mc.alk.arena.util.Log;

public class StateController {
	private final ArenaMatchQueue amq;
	private LobbyController lobbies = LobbyController.INSTANCE;
	private LobbyController waitrooms = LobbyController.INSTANCE;


	public StateController(ArenaMatchQueue amq){
		this.amq = amq;
	}

	public JoinResult join(TeamJoinObject tqo, boolean shouldStart) {
		JoinResult jr = amq.join(tqo,shouldStart );
		MatchParams mp = tqo.getMatchParams();
		/// who is responsible for doing what
		Log.debug(" Join status = " + jr.status +"    " + tqo.getTeam() + "   " + tqo.getTeam().getId() +" --"
				+ ", haslobby="+mp.hasLobby() +"  ,wr="+(mp.getTransitionOptions().hasOptionAt(MatchState.ONJOIN, TransitionOption.TELEPORTWAITROOM))+"  "+
				"   --- hasArena=" + tqo.getJoinOptions().hasArena());
		switch(jr.status){
		case ADDED_TO_ARENA_QUEUE:
		case ADDED_TO_QUEUE:
			break;
		case NONE:
			break;
		case ERROR:
		case ADDED_TO_EXISTING_MATCH:
		case STARTED_NEW_GAME:
			return jr;
		default:
			break;
		}
		if (mp.getTransitionOptions().hasOptionAt(MatchState.ONJOIN, TransitionOption.TELEPORTLOBBY)){
			lobbies.joinLobby(tqo.getMatchParams().getType(), tqo.getTeam());
		}
		if (tqo.getJoinOptions().hasArena()){
			if (mp.getTransitionOptions().hasOptionAt(MatchState.ONJOIN, TransitionOption.TELEPORTWAITROOM)){
				waitrooms.joinWaitroom(tqo.getJoinOptions().getArena(), tqo.getTeam());
			} else if (mp.getTransitionOptions().hasOptionAt(MatchState.ONJOIN, TransitionOption.TELEPORTIN)){
				tqo.getJoinOptions().getArena().teamJoining(tqo.getTeam());
			}
		}
		return jr;
	}

}

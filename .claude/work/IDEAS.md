# Ideas

A notepad for any idea about the project: a feature, a name, a design, something to look into. An idea doesn't need to be good or ever be built. It's written here so it isn't forgotten.
If one is chosen for the plan, add it to `REQUIREMENTS.md` and mark it "planned" here.

| Date | Idea | From | Notes | Status |
|---|---|---|---|---|
| 2026-10-27 | Finish voice and video calls in the chat | Claude | `chat.html` has the call box and video tiles, and `ChatController` relays `call/sdp`, `call/ice` and `call/action`. But `chat.js` only toggles the call UI ("UI only"): no `RTCPeerConnection` or `getUserMedia`, so no call ever connects. Needs WebRTC in `chat.js`, and probably a STUN/TURN server for members behind strict networks. | just a note |
| 2026-10-27 | A page to set what a member is looking for | Claude | `Search_Profile` exists and `MainController` reads it for matching, but no route serves `templates/profile/search.html` and nothing saves a `Search_Profile`. A member cannot set their search preferences, so matching works from empty criteria. | just a note |
| 2026-10-27 | Recreate the views and procedures in a script in `db/` | Claude | Matches, the chat list and profile editing read `profile_view`, `chatlist_item`, `message_view`, `MatchUsers`, `FetchUserdata` and others, which only existed in the old database. A script in the repository would let any new database be set up from scratch (also NFR-19). Open work in `TASKLIST.md`. | just a note |
| 2026-10-27 | Lock down the actuator | Claude | `application.properties` exposes every actuator endpoint (`management.endpoints.web.exposure.include=*`), including `env` and `heapdump`, which can reveal secrets (NFR-4, NFR-7). Not checked whether the login protects `/actuator`. Expose only `health` (and `info`), or require the admin role. | just a note |
| 2026-10-27 | Tidy the logging and dead settings | Claude | `logging.level.org.springframework.security=DEBUG` and the `JdbcTemplate` DEBUG line flood the log and can write personal data into it (NFR-21). The three `spring.jpa.*` lines do nothing: SolarFramework builds its own Hibernate and never reads them. | just a note |
| 2026-10-27 | Real tests | Claude | The only test is `MyDatingSiteApplicationTests`, which checks that the app starts. Matching, sending, editing and deleting messages, reactions and attachment upload have none (NFR-18). The test class still carries the old name. | just a note |
| 2026-10-27 | One Bootstrap version | Claude | `matches.html` loads Bootstrap CSS 5.3.2; every other page loads 5.3.3. | just a note |
| 2026-10-27 | Finish the rename to MauLamour | Claude | The main class is now `MauLabourApplication` ("Labour", not "Lamour"; check whether that was meant). The GitHub repository is still `MyDatingSite`, and the package is still `my.dating.app`. | just a note |

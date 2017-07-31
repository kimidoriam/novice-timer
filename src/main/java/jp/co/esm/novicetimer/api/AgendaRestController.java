package jp.co.esm.novicetimer.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jp.co.esm.novicetimer.domain.Agenda;
import jp.co.esm.novicetimer.domain.Subject;
import jp.co.esm.novicetimer.domain.TimerState;
import jp.co.esm.novicetimer.service.AgendaService;

@RestController
@RequestMapping("api/agendas")
public class AgendaRestController {
    @Autowired
    private AgendaService agendaService;

    @GetMapping
    public List<Agenda> getAgendas() {
        return agendaService.findAll();
    }

    @GetMapping("{id}")
    public Agenda getAgenda(@PathVariable Integer id) {
        return agendaService.findOne(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Agenda creatAgenda(@RequestBody Agenda agenda) {
        return agendaService.create(agenda);
    }

    /**
     * PUTリクエストを受けてアジェンダを更新する。
     * <p>
     * idに対応するアジェンダを、リクエストボディに渡されたアジェンダに変更する。<br>
     * HTTPステータスは、更新された場合は201を返す。idが不正の場合は404を返す。
     *
     * @param agenda 更新するアジェンダの内容
     * @param id 更新されるアジェンダのid
     * @return 更新後のアジェンダ
     */
    @PutMapping("{id}")
    public Agenda editAgenda(@PathVariable Integer id, @RequestBody Agenda agenda) {
        agenda.setId(id);
        return agendaService.update(agenda);
    }

    /**
     * PUTリクエストを受けてアジェンダのサブジェクトを1つ更新する。
     * <p>
     * idに対応するアジェンダのnumber番目のサブジェクトを、リクエストボディに渡されたサブジェクトに変更する。<br>
     * HTTPステータスは、更新された場合は201を返す。idが不正の場合と、numberが不正の場合は404を返す。
     *
     * @param subject 更新するサブジェクトの内容
     * @param id 更新されるアジェンダのid
     * @param number 更新されるサブジェクトの番目
     * @return 更新後のアジェンダ
     */
    @PutMapping("{id}/subjects/{number}")
    public Agenda editSubject(@PathVariable Integer id,
        @PathVariable Integer number,
        @RequestBody Subject subject) {

        return agendaService.updateSubject(id, number, subject);
    }

    @PutMapping("{id}/subjects/{number}/timers")
    public ResponseEntity<String> operateTimer(@PathVariable Integer id,
        @PathVariable Integer number,
        @RequestBody TimerState timerState) {

        try {
            agendaService.changeTimerState(id, number, timerState.getState());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ IllegalArgumentException.class })
    @ResponseBody
    public void handleIllegalArgumentException() {
    }
}

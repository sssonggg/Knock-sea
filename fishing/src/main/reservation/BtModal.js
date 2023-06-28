import React, { useEffect, useState } from 'react';
import "../scss/Calendar.scss"
import "../fishingspot/RvScss/RvModal.scss"
import { getLoginUserInfo } from "../util/login-util";
import ClassCalendar from '../class/ClassCalendar';

const handleLogin = (e) => {
    e.preventDefault();
  
      // 회원가입 서버 요청
    };
    
  // 렌더링 후 실행함수
// timeList, price, address
function BtModal({closeModal, sDetail}) {
  const [token, setToken] = useState(getLoginUserInfo().token);
  const listSize=sDetail.timeList.length-1;
  const startTime=sDetail.timeList[0].timeDate; //시작날짜
  const EndTime=sDetail.timeList[listSize].timeDate; //마지막 날짜

  const [selectedDate, setSelectedDate] = useState(null);
  const [count, setCount] = useState(0);
  const [selectedTime, setSelectedTime] = useState(null);
  const [classTimes, setClassTimes] = useState([]);
  const[timeIndex,setTimeIndex] = useState(0);

  const handleIncrease = () => {
    setCount(count + 1);
  };

  const handleDecrease = () => {
    if (count > 0) {
      setCount(count - 1);
    }
  };

  const formattedDate = selectedDate ? selectedDate.toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric' }) : '';

  const handleDateChange = (date) => {
    setSelectedDate(date);
  };

  const handleTimeChange = (time,timeIndex) => {
    setSelectedTime(time);
    setTimeIndex(timeIndex);
  }    
    const API_BASE_URL = 'http://localhost:8012/api/v1/reservation';
  
  const handlePayment=()=>{
    console.log("token",token.userId);
    const reservation = {
      reservationType : "SHIP",
      reservationDate : formattedDate, 
      reservationAddress : sDetail.fullAddress,
      reservationUserCount : count,
      reservationPrice :  sDetail.price,
      // eduLevel : sDetail.eduLevel,
      // userId : token.userId,
      productId : sDetail.productId,
      reservationTimeId : timeIndex 
    };
    
    console.log("click button : ", reservation);


    const requestHeader = {
      'content-type': 'application/json',
      'Authorization': 'Bearer ' + token
    };
    

        fetch(API_BASE_URL, {
        method: 'POST',
        headers:  requestHeader,
        body: JSON.stringify(reservation)
      })
      .then(res => {
        if(res.status === 200) return res.json();
        else if (res.status === 401) {
          alert('실패');
        }
      })
      // .then(json => {
      //   json && setTodos(json.todos);
      // });
  }






    return (
        <div className="Rv-modal-overlay" >
          <div className="Rv-modal-box">
            <button onClick={closeModal} className='close-btn'><img src='https://cdn-icons-png.flaticon.com/128/7778/7778647.png'/></button>
            <h1 className='select-date'>참여 일정을 선택해주세요😀</h1>
            <hr style={{marginTop:'5px'}}/>
            <div className='calendar'>
              <ClassCalendar className='datePicker' handleDateChange={handleDateChange} startTime={startTime} EndTime={EndTime}/>
            </div>

                {sDetail.timeList.map((time, index) => {
                  const timeDate = new Date(time.timeDate);
                  const selectedLocalDate = selectedDate ? new Date(selectedDate.getTime() - (selectedDate.getTimezoneOffset() * 60000)) : null;

                  if (selectedLocalDate && timeDate.getTime() === selectedLocalDate.getTime()) {
                    return (
                      <div className='class-select-time' key={sDetail.timeList[index].timeId}>
                        <div className='selected-time' onClick={() => handleTimeChange(`${time.timeStart}~${time.timeEnd}`,`${sDetail.timeList[index].timeId}` )}>
                          {time.timeStart}~{time.timeEnd}
                        </div>
                        <div className='selected-time'>남은 인원: {sDetail.timeList[index].timeMaxUser - sDetail.timeList[index].timeCurrentUser}명</div>
                        
                        {/* {classTimes.map((time, index) => (
                          <div key={index} className='selected-time' onClick={() => handleTimeChange(time,oneEdu.timeList[index].timeId)}>
                            {time}
                          </div>
                        ))} */}
                      </div>
                    );
                  }
                  return null;
                })}
              

            <div className='result'>
                  <ul  className='result-ul'>
                    <li className='result-li'>
                      <span>선택 날짜</span>
                      <span>{formattedDate}</span>
                    </li>
                     <li className='result-li'>
                      <span>선택 시간</span>
                      <span>{selectedTime}</span>
                     </li>
                    <li className='result-li'>
                      <span>인원</span>
                      <label htmlFor="counter" className="NumberCounter">
                        <button type="button" className="NumberCounter__button" onClick={handleDecrease}>-</button> 
                        <input type="text" name="counter" className="NumberCounter__input" value={count} readOnly/> 
                        <button type="button" className="NumberCounter__button" onClick={handleIncrease}>+</button>
                      </label>
                    </li>
                  </ul>
                  <div className='total-price'>
                    <span>결제 총계 </span>
                    <span> {count*sDetail.price}원 </span>
                  </div>
                <p className='total-result'>{formattedDate} {selectedTime} / {count}명</p>
                <button className='class-pay-btn custom-button' onClick={handlePayment}>결제하기</button>
            </div>
          </div>
        </div>
      );

    }
  export default BtModal;
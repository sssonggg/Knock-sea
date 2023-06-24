import React, { useState, useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import './MpScss/MpInquiryD.scss';
import { getLoginUserInfo } from '../util/login-util';

const MpInquiryD = () => {
  const { inquiryId } = useParams();
  const [answerDetails, setAnswerDetails] = useState('');
  const [answers, setAnswers] = useState([]);
  const [data, setData] = useState(null);

  const handleAnswerChange = (e) => {
    setAnswerDetails(e.target.value);
  };

  const handleSubmitAnswer = (e) => {
    e.preventDefault();
    // TODO: Submit the answer
    console.log(answerDetails);
    console.log(inquiryId);
    console.log(answers);
    // Reset the answer field
    setAnswerDetails('');
  };

  // useEffect(() => {
  //   fetchAnswers();
  // }, []);

  useEffect(() => {
    const fetchAnswers = async () => {
      try {
        const response = await fetch(`http://localhost:8012/api/v1/answers/${inquiryId}`);
        if (response.ok) {
          const data = await response.json();
          setData(data);
          console.log(data);
        } else {
          throw new Error('Failed to fetch answers');
        }
      } catch (error) {
        console.log(error);
      }
    };
    console.log(data);
    fetchAnswers();
  }, [inquiryId]);

  // fetch(`http://localhost:8012/api/v1/answers/${inquiryId}`)
  // .then((response) => response.json())
  // .then((data) => console.log(data));

  // const fetchAnswers = async () => {
  //   try {
  //     const response = await fetch(`http://localhost:8012/api/v1/answers/${inquiryId}`, {
  //       method: 'GET',
  //     });
  //     if (response.ok) {
  //       const data = await response.json();
  //       setAnswers(data.answers);
  //       console.log(data.answers);
  //     } else {
  //       throw new Error('Failed to fetch answers');
  //     }
  //   } catch (error) {
  //     console.log(error);
  //   }
  // };

  return (
    <section>
      <div className="adminbox">
        <div className="mgbox">
          <div className="mgtitle">
            <p>관리자</p>
          </div>
          <div className="mglist">
            <div>
              <Link to="/admin">배 검증요청</Link>
            </div>
            <div className="ch2">
              <Link to="/adminFS">낚시터 검증요청</Link>
            </div>
            <div>
              <Link to="/adminCS">문의 현황</Link>
            </div>
          </div>
        </div>
        <div className="mgcontentbox">
          <div className="ctntitle">KNOCK_SEA 관리자 화면 (답변하기)</div>
          <div className="ctntext">
            <div className="ctntextbox1">
              <div className="answertext" >
                {/* 작성자가 문의한 글 보여주는 곳 */}
                {data.inquiryDetails}
              </div>
              <div className="adminreplyinput">
                <textarea value={answerDetails} onChange={handleAnswerChange} />
              </div>
            </div>
          </div>
          <button onClick={handleSubmitAnswer}>답변완료</button>
        </div>
      </div>
    </section>
  );
};

export default MpInquiryD;